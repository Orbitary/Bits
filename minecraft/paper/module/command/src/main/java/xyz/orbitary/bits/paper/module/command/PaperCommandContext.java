/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.module.command;

import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * The source of a Paper command, passed to command methods that declare it.
 *
 * @since 0.1.6
 */
public final class PaperCommandContext {

    private final CommandSourceStack source;

    /**
     * @param source the Brigadier source of the executing command
     *
     * @since 0.1.6
     */
    public PaperCommandContext(CommandSourceStack source) {
        this.source = source;
    }

    /**
     * @return the sender of the command
     *
     * @since 0.1.6
     */
    public CommandSender sender() {
        return source.getBukkitSender();
    }

    /**
     * @return the sender of the command, which must be a player
     *
     * @throws IllegalStateException if the sender is not a player
     *
     * @since 0.1.6
     */
    public Player requirePlayer() {
        if (!(sender() instanceof Player player)) throw new IllegalStateException("Command sender must be a player");
        return player;
    }

    /**
     * @param message the message to send to the sender
     *
     * @since 0.1.6
     */
    public void respond(Component message) {
        sender().sendMessage(message);
    }

}
