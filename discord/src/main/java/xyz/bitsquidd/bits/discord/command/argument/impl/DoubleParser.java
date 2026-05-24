package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

public class DoubleParser extends JdaArgumentParser<Double> {
    public Class<Double> type() { return Double.class; }
    public OptionType optionType() { return OptionType.NUMBER; }
    public Double resolve(OptionMapping m, JdaCommandContext ctx) { return m.getAsDouble(); }
}
