package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

public class MemberParser extends JdaArgumentParser<Member> {
    public Class<Member> type() { return Member.class; }
    public OptionType optionType() { return OptionType.USER; }
    public Member resolve(OptionMapping m, JdaCommandContext ctx) { return m.getAsMember(); }
}
