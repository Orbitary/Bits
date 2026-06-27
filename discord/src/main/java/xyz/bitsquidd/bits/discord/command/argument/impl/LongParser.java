/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;


public class LongParser extends JdaArgumentParser<Long> {
    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.INTEGER;
    }

    @Override
    public Long resolve(OptionMapping m, JdaCommandContext ctx) {
        return m.getAsLong();
    }

}
