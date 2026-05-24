package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;


public class AttachmentParser extends JdaArgumentParser<Message.Attachment> {
    @Override
    public Class<Message.Attachment> type() {
        return Message.Attachment.class;
    }

    @Override
    public OptionType optionType() {
        return OptionType.ATTACHMENT;
    }

    @Override
    public Message.Attachment resolve(OptionMapping m, JdaCommandContext ctx) {
        return m.getAsAttachment();
    }

}
