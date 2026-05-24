package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

public class IntegerParser extends JdaArgumentParser<Integer> {
    public Class<Integer> type() { return Integer.class; }
    public OptionType optionType() { return OptionType.INTEGER; }
    public Integer resolve(OptionMapping m, JdaCommandContext ctx) { return (int) m.getAsLong(); }
}
