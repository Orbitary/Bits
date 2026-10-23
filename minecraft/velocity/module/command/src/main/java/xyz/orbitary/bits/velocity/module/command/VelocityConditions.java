/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.velocity.module.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import xyz.orbitary.bits.module.command.Condition;


/**
 * Conditions for Velocity commands. Use them with {@link xyz.orbitary.bits.module.command.Guard}.
 *
 * @since 0.1.6
 */
public final class VelocityConditions {

    private VelocityConditions() {}

    /**
     * Passes only when the sender is a player.
     *
     * @since 0.1.6
     */
    public static final class PlayerSender implements Condition<CommandSource> {
        @Override
        public boolean test(CommandSource source) {
            return source instanceof Player;
        }
    }

    /**
     * Passes only when the sender is the proxy console.
     *
     * @since 0.1.6
     */
    public static final class ConsoleSender implements Condition<CommandSource> {
        @Override
        public boolean test(CommandSource source) {
            return source instanceof ConsoleCommandSource;
        }
    }

}
