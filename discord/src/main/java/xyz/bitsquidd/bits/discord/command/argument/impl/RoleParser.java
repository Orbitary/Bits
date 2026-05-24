package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;


public class RoleParser extends JdaArgumentParser<Role> {
    @Override
    public Class<Role> type() {
        return Role.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.ROLE;
    }

    @Override
    public Role resolve(OptionMapping m, JdaCommandContext ctx) {
        return m.getAsRole();
    }

}
