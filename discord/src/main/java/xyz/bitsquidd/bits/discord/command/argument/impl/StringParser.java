package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

public class StringParser extends JdaArgumentParser<String> {
    public Class<String> type() { return String.class; }
    public OptionType optionType() { return OptionType.STRING; }
    public String resolve(OptionMapping m, JdaCommandContext ctx) { return m.getAsString(); }
}
