/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.module.command;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import xyz.orbitary.bits.module.command.Condition;


/**
 * Conditions for Paper commands. Use them with {@link xyz.orbitary.bits.module.command.Guard}.
 *
 * @since 0.1.6
 */
public final class PaperConditions {

    private PaperConditions() {}

    /**
     * Passes only when the sender is a player.
     *
     * @since 0.1.6
     */
    public static final class PlayerSender implements Condition<CommandSourceStack> {
        @Override
        public boolean test(CommandSourceStack source) {
            return source.getBukkitSender() instanceof Player;
        }
    }

    /**
     * Passes only when the sender is the server console.
     *
     * @since 0.1.6
     */
    public static final class ConsoleSender implements Condition<CommandSourceStack> {
        @Override
        public boolean test(CommandSourceStack source) {
            return source.getBukkitSender() instanceof ConsoleCommandSender;
        }
    }

    /**
     * Passes only when the sender is a player in the given game mode.
     *
     * @since 0.1.6
     */
    public abstract static class PlayerGamemode implements Condition<CommandSourceStack> {

        private final GameMode gameMode;

        protected PlayerGamemode(GameMode gameMode) {
            this.gameMode = gameMode;
        }

        @Override
        public boolean test(CommandSourceStack source) {
            return source.getBukkitSender() instanceof Player player && player.getGameMode() == gameMode;
        }

    }

    /**
     * @since 0.1.6
     */
    public static final class Creative extends PlayerGamemode {
        public Creative() {
            super(GameMode.CREATIVE);
        }
    }

    /**
     * @since 0.1.6
     */
    public static final class Survival extends PlayerGamemode {
        public Survival() {
            super(GameMode.SURVIVAL);
        }
    }

    /**
     * @since 0.1.6
     */
    public static final class Adventure extends PlayerGamemode {
        public Adventure() {
            super(GameMode.ADVENTURE);
        }
    }

    /**
     * @since 0.1.6
     */
    public static final class Spectator extends PlayerGamemode {
        public Spectator() {
            super(GameMode.SPECTATOR);
        }
    }

}
