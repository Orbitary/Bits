package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

public class LongParser extends JdaArgumentParser<Long> {
    public Class<Long> type() { return Long.class; }
    public OptionType optionType() { return OptionType.INTEGER; }
    public Long resolve(OptionMapping m, JdaCommandContext ctx) { return m.getAsLong(); }
}
