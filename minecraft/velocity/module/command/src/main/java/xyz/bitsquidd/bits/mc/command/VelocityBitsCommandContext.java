/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;

import com.velocitypowered.api.proxy.Player;

import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;


public class VelocityBitsCommandContext extends BitsCommandContext<CommandSource> {
    public VelocityBitsCommandContext(CommandContext<CommandSource> brigadierContext) {
        super(brigadierContext);
    }

    @Override
    protected BitsCommandSourceContext<CommandSource> createSourceContext(CommandContext<CommandSource> brigadierContext) {
        return VelocityBitsCommandManager.get().createSourceContext(brigadierContext.getSource());
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandSource getSender() {
        return source.getSender();
    }


    public Player requirePlayer() {
        if (!(getSender() instanceof Player player)) throw new IllegalStateException("Command sender must be a player");
        return player;
    }

}
