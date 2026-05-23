/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import xyz.bitsquidd.bits.BitsDiscord;
import xyz.bitsquidd.bits.log.Logger;

import java.util.List;


public class DiscordNotifier {
    public void sendDm(long userId, MessageEmbed embed) {
        sendDm(userId, List.of(embed), List.of());
    }

    public void sendDm(long userId, List<MessageEmbed> embeds) {
        sendDm(userId, embeds, List.of());
    }

    public void sendDm(long userId, MessageEmbed embed, List<LayoutComponent> components) {
        sendDm(userId, List.of(embed), components);
    }

    public void sendDm(long userId, List<MessageEmbed> embeds, List<LayoutComponent> components) {
        BitsDiscord.jda().openPrivateChannelById(userId).queue(
            channel -> channel.sendMessageEmbeds(embeds)
                .setComponents(components)
                .queue(
                    null,
                    err -> Logger.warn("Failed to send DM to " + userId + ": " + err.getMessage())
                ),
            err -> Logger.warn("Could not open DM channel for " + userId + ": " + err.getMessage())
        );
    }
}
