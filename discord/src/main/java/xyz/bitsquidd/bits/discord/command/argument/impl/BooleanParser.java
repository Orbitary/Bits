package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;


public class BooleanParser extends JdaArgumentParser<Boolean> {
    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.BOOLEAN;
    }

    @Override
    public Boolean resolve(OptionMapping m, JdaCommandContext ctx) {
        return m.getAsBoolean();
    }

}
