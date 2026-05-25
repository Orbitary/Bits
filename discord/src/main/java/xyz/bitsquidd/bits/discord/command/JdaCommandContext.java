/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Wraps a {@link SlashCommandInteractionEvent} with convenience reply helpers.
 */
public final class JdaCommandContext {
    private final SlashCommandInteractionEvent event;

    public JdaCommandContext(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void reply(MessageEmbed embed) {
        event.replyEmbeds(embed).queue();
    }

    public void reply(String message) {
        event.reply(message).queue();
    }

    public void replyEphemeral(MessageEmbed embed) {
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    public void replyEphemeral(String message) {
        event.reply(message).setEphemeral(true).queue();
    }

    public void deferReply(boolean ephemeral) {
        event.deferReply(ephemeral).queue();
    }

    public Member getMember() {
        return event.getMember();
    }

    public User getUser() {
        return event.getUser();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public SlashCommandInteractionEvent raw() {
        return event;
    }
}
