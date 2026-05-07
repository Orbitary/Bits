/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;


/**
 * A wrapper around a Bukkit player, allowing us to abstract the platform.
 */
public abstract class PaperReceiver implements Receiver {
    public static PaperReceiver from(final Player player) {
        return new PaperReceiver() {
            @Override
            protected void sendPacket(Packet<?> packet) {
                ((CraftPlayer)player).getHandle().connection.send(packet);
            }

            @Override
            public UUID getUniqueId() {
                return player.getUniqueId();
            }
        };
    }

    protected abstract void sendPacket(final Packet<?> packet);

    protected void sendPackets(final Collection<? extends Packet<?>> packets) {
        packets.forEach(this::sendPacket);
    }

}
