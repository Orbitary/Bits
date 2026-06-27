/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

import java.util.Objects;


public class MemberParser extends JdaArgumentParser<Member> {
    @Override
    public Class<Member> type() {
        return Member.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.USER;
    }

    @Override
    public Member resolve(OptionMapping m, JdaCommandContext ctx) {
        return Objects.requireNonNull(m.getAsMember());
    }

}
