package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;


public class UserParser extends JdaArgumentParser<User> {
    @Override
    public Class<User> type() {
        return User.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.USER;
    }

    @Override
    public User resolve(OptionMapping m, JdaCommandContext ctx) {
        return m.getAsUser();
    }

}
