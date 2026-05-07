/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;

public class PaperBitsCommandSourceContext extends BitsCommandSourceContext<CommandSourceStack> {
    public PaperBitsCommandSourceContext(CommandSourceStack source) {
        super(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandSender getSender() {
        return source.getSender();
    }

    /**
     * Returns a non-null player if the command sender is a player, otherwise throws an exception.
     */
    public Player requirePlayer() {
        if (!(getSender() instanceof Player player)) throw new IllegalStateException("Command sender must be a player");
        return player;
    }

    /**
     * Returns the location this command was executed at.
     */
    public Location getLocation() {
        return source.getLocation();
    }

}
