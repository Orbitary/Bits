/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.velocity.module.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;


/**
 * The source of a Velocity command, passed to command methods that declare it.
 *
 * @since 0.1.6
 */
public final class VelocityCommandContext {

    private final CommandSource sender;

    /**
     * @param sender the sender of the executing command
     *
     * @since 0.1.6
     */
    public VelocityCommandContext(CommandSource sender) {
        this.sender = sender;
    }

    /**
     * @return the sender of the command
     *
     * @since 0.1.6
     */
    public CommandSource sender() {
        return sender;
    }

    /**
     * @return the sender of the command, which must be a player
     *
     * @throws IllegalStateException if the sender is not a player
     *
     * @since 0.1.6
     */
    public Player requirePlayer() {
        if (!(sender instanceof Player player)) throw new IllegalStateException("Command sender must be a player");
        return player;
    }

}
