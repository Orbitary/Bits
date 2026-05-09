/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.velocitypowered.api.command.CommandSource;

import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;

public class VelocityBitsCommandSourceContext extends BitsCommandSourceContext<CommandSource> {
    public VelocityBitsCommandSourceContext(CommandSource source) {
        super(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandSource getSender() {
        return source;
    }

}
