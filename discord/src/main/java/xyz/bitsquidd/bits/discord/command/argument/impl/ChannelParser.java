/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;


public class ChannelParser extends JdaArgumentParser<GuildChannelUnion> {
    @Override
    public Class<GuildChannelUnion> type() {
        return GuildChannelUnion.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.CHANNEL;
    }

    @Override
    public GuildChannelUnion resolve(OptionMapping m, JdaCommandContext ctx) {
        return m.getAsChannel();
    }

}
