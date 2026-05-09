/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.waypoint;

import net.kyori.adventure.key.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import xyz.bitsquidd.bits.mc.sendable.PaperReceiver;
import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractLocationWaypoint;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractTransmittingWaypoint;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractWaypoint;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.WaypointCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.WaypointManager;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PaperWaypointManager extends WaypointManager {
    private final Map<UUID, Map<UUID, WaypointTransmitter.Connection>> transmittors = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> tracked = new ConcurrentHashMap<>();

    @Override
    protected void render(Receiver receiver, WaypointCollection collection) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        List<Packet<?>> waypointPackets = new ArrayList<>();

        collection.getAll().forEach(waypointHandle -> {
            AbstractWaypoint waypointDefinition = waypointHandle.definition();

            Key assetKey = waypointDefinition.getAssetKey();
            ResourceKey<WaypointStyleAsset> nmsAssetKey = ResourceKey.create(
              WaypointStyleAssets.ROOT_ID,
              Identifier.parse(assetKey.asString())
            );
            Waypoint.Icon icon = ReflectionUtils.Instance.tryCreate(
              Waypoint.Icon.class,
              nmsAssetKey,
              waypointDefinition.getColor()
            ).orElseThrow();

            UUID handleUUID = waypointHandle.uuid();

            if (waypointDefinition instanceof AbstractLocationWaypoint locationalWaypoint) {
                Vector3i position = locationalWaypoint.getPosition();

                waypointPackets.add(ClientboundTrackedWaypointPacket.addWaypointPosition(
                  handleUUID, icon, new Vec3i(position.x, position.y, position.z)
                ));

                tracked.computeIfAbsent(receiver.getUniqueId(), _ -> ConcurrentHashMap.newKeySet()).add(handleUUID);
            } else if (waypointDefinition instanceof AbstractTransmittingWaypoint transmittingWaypoint) {
                UUID receiverUUID = receiver.getUniqueId();
                Map<UUID, WaypointTransmitter.Connection> receiverConnections =
                  transmittors.computeIfAbsent(receiverUUID, _ -> new ConcurrentHashMap<>());

                WaypointTransmitter.Connection existing = receiverConnections.get(handleUUID);

                if (existing == null) {
                    Player player = Bukkit.getPlayer(receiverUUID);
                    if (player == null) return;
                    ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();

                    resolveConnection(transmittingWaypoint.getTransmitterUUID(), icon, handleUUID, serverPlayer)
                      .ifPresentOrElse(
                        connection -> {
                            receiverConnections.put(handleUUID, connection);
                            connection.connect();
                        },
                        waypointHandle::bits$markForExpire
                      );
                } else if (existing.isBroken()) {
                    existing.disconnect();
                    receiverConnections.remove(handleUUID);
                    waypointHandle.bits$markForExpire();
                } else {
                    existing.update();
                }
            } else {
                throw new IllegalStateException("Unknown waypoint type: " + waypointDefinition.getClass());
            }
        });

        paperReceiver.sendPackets(waypointPackets);
    }


    @Override
    public void onExpire(Receiver receiver, SendableHandle<? extends AbstractWaypoint> handle) {
        super.onExpire(receiver, handle);
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        AbstractWaypoint waypointDefinition = handle.definition();
        UUID waypointUUID = handle.uuid();
        UUID receiverUUID = receiver.getUniqueId();

        if (waypointDefinition instanceof AbstractLocationWaypoint) {
            paperReceiver.sendPacket(ClientboundTrackedWaypointPacket.removeWaypoint(waypointUUID));
            Set<UUID> receiverTracked = tracked.get(receiverUUID);
            if (receiverTracked != null) receiverTracked.remove(waypointUUID);
        } else if (waypointDefinition instanceof AbstractTransmittingWaypoint) {
            Map<UUID, WaypointTransmitter.Connection> receiverConnections = transmittors.get(receiverUUID);
            if (receiverConnections != null) {
                WaypointTransmitter.Connection connection = receiverConnections.remove(waypointUUID);
                if (connection != null) connection.disconnect();
            }
        } else {
            throw new IllegalStateException("Unknown waypoint type: " + waypointDefinition.getClass());
        }
    }


    /**
     * Resolves a {@link WaypointTransmitter.Connection} from a transmitter UUID.
     * Returns a connection that uses the waypoint's custom icon, delegating {@code isBroken()} to
     * the entity's native connection. Override to support packet entity systems.
     * Returns empty if the entity cannot be found — the waypoint will be expired.
     */
    protected Optional<WaypointTransmitter.Connection> resolveConnection(UUID transmitterUUID, Waypoint.Icon icon, UUID handleUUID, ServerPlayer player) {
        Entity entity = Bukkit.getEntity(transmitterUUID);
        if (entity instanceof CraftLivingEntity craftLiving) {
            LivingEntity nmsEntity = craftLiving.getHandle();
            return nmsEntity.makeWaypointConnectionWith(player)
              .map(inner -> new IconConnection(handleUUID, icon, nmsEntity, player, inner));
        }
        return Optional.empty();
    }


    private static class IconConnection implements WaypointTransmitter.Connection {
        private final UUID uuid;
        private final Waypoint.Icon icon;
        private final LivingEntity entity;
        private final ServerPlayer serverPlayer;
        private final WaypointTransmitter.Connection inner;
        private @Nullable BlockPos lastPos;

        IconConnection(UUID uuid, Waypoint.Icon icon, LivingEntity entity, ServerPlayer serverPlayer, WaypointTransmitter.Connection inner) {
            this.uuid = uuid;
            this.icon = icon;
            this.entity = entity;
            this.serverPlayer = serverPlayer;
            this.inner = inner;
        }

        @Override
        public void connect() {
            lastPos = entity.blockPosition();
            serverPlayer.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(
              uuid, icon, new Vec3i(lastPos.getX(), lastPos.getY(), lastPos.getZ())
            ));
        }

        @Override
        public void update() {
            BlockPos pos = entity.blockPosition();
            if (Objects.equals(pos, lastPos)) return;
            lastPos = pos;
            serverPlayer.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(
              uuid, icon, new Vec3i(pos.getX(), pos.getY(), pos.getZ())
            ));
        }

        @Override
        public void disconnect() {
            serverPlayer.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(uuid));
        }

        @Override
        public boolean isBroken() {
            return inner.isBroken();
        }

    }


    @Override
    protected void cleanupReceiver(Receiver receiver) {
        super.cleanupReceiver(receiver);
        UUID uuid = receiver.getUniqueId();

        transmittors.remove(uuid);
        tracked.remove(uuid);
    }

}
