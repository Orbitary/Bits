package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

public class ChannelParser extends JdaArgumentParser<GuildChannelUnion> {
    public Class<GuildChannelUnion> type() { return GuildChannelUnion.class; }
    public OptionType optionType() { return OptionType.CHANNEL; }
    public GuildChannelUnion resolve(OptionMapping m, JdaCommandContext ctx) { return m.getAsChannel(); }
}
