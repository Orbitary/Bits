/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;


public class PaperBitsCommandContext extends BitsCommandContext<CommandSourceStack> {
    public PaperBitsCommandContext(CommandContext<CommandSourceStack> brigadierContext) {
        super(brigadierContext);
    }

    @Override
    protected BitsCommandSourceContext<CommandSourceStack> createSourceContext(CommandContext<CommandSourceStack> brigadierContext) {
        return PaperBitsCommandManager.get().createSourceContext(brigadierContext.getSource());
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandSender getSender() {
        return source.getSender();
    }

    public Player requirePlayer() {
        if (!(getSender() instanceof Player player)) throw new IllegalStateException("Command sender must be a player");
        return player;
    }

    public Location getLocation() {
        return source.getSource().getLocation();
    }

}
