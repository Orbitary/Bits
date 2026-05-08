/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import net.kyori.adventure.key.Key;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.joml.Vector3i;

import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractLocationalWaypoint;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractTransmittingWaypoint;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractWaypoint;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.WaypointCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.WaypointManager;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

            if (waypointDefinition instanceof AbstractLocationalWaypoint locationalWaypoint) {
                Vector3i position = locationalWaypoint.getPosition();

                waypointPackets.add(ClientboundTrackedWaypointPacket.addWaypointPosition(
                  handleUUID, icon, new Vec3i(position.x, position.y, position.z)
                ));

                tracked.computeIfAbsent(receiver.getUniqueId(), _ -> ConcurrentHashMap.newKeySet()).add(handleUUID);
            } else if (waypointDefinition instanceof AbstractTransmittingWaypoint transmittingWaypoint) {
                // TODO ADD TO TRANSMITTORS
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

        if (waypointDefinition instanceof AbstractLocationalWaypoint) {
            paperReceiver.sendPacket(ClientboundTrackedWaypointPacket.removeWaypoint(waypointUUID));
        } else if (waypointDefinition instanceof AbstractTransmittingWaypoint transmittingWaypoint) {
            WaypointTransmitter.Connection connection = transmittors.getOrDefault(receiver.getUniqueId(), Map.of()).remove(waypointUUID);
            if (connection != null) connection.disconnect();
        } else {
            throw new IllegalStateException("Unknown waypoint type: " + waypointDefinition.getClass());
        }
    }

}
