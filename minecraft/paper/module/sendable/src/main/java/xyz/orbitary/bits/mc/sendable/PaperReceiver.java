/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


/**
 * A wrapper around a Bukkit player, allowing us to abstract the platform.
 */
public interface PaperReceiver extends Receiver {

    static PaperReceiver from(final Player player) {
        return new PaperReceiver() {
            @Override
            public void sendPackets(Packet<?> packets) {
                ((CraftPlayer)player).getHandle().connection.send(packets);
            }

            @Override
            public void sendPackets(Collection<? extends Packet<?>> packets) {
                packets.forEach(this::sendPackets);
            }


            @Override
            public Player getPlayer() {
                return player;
            }


            @Override
            public int hashCode() {
                return getUniqueId().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof PaperReceiver other && getUniqueId().equals(other.getUniqueId());
            }
        };
    }

    @Override
    default UUID getUniqueId() {
        return getPlayer().getUniqueId();
    }

    Player getPlayer();

    void sendPackets(final Packet<?> packets);

    default void sendPackets(final Packet<?>... packets) {
        sendPackets(List.of(packets));
    }

    void sendPackets(final Collection<? extends Packet<?>> packets);

}
