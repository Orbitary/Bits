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


public class StringParser extends JdaArgumentParser<String> {
    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.STRING;
    }

    @Override
    public String resolve(OptionMapping m, JdaCommandContext ctx) {
        return m.getAsString();
    }

}
