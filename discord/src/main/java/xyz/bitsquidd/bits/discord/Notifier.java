/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord;

import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import xyz.bitsquidd.bits.BitsDiscord;
import xyz.bitsquidd.bits.log.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public final class Notifier {
    private Notifier() {}

    public static final class DM {
        private DM() {}

        public static CompletableFuture<Message> send(long userId, MessageEmbed embed) {
            return send(userId, List.of(embed), List.of());
        }

        public static CompletableFuture<Message> send(long userId, List<? extends MessageEmbed> embeds) {
            return send(userId, embeds, List.of());
        }

        public static CompletableFuture<Message> send(long userId, MessageEmbed embed, List<? extends MessageTopLevelComponent> components) {
            return send(userId, List.of(embed), components);
        }

        public static CompletableFuture<Message> send(long userId, List<? extends MessageEmbed> embeds, List<? extends MessageTopLevelComponent> components) {
            CompletableFuture<Message> future = new CompletableFuture<Message>();

            BitsDiscord.jda().openPrivateChannelById(userId).queue(
              channel -> {
                  channel.sendMessageEmbeds(embeds)
                    .setComponents(components)
                    .queue(
                      future::complete,
                      err -> {
                          Logger.warn("Failed to send DM to " + userId + ": " + err.getMessage());
                          future.completeExceptionally(err);
                      }
                    );
              },
              err -> {
                  Logger.warn("Could not open DM channel for " + userId + ": " + err.getMessage());
                  future.completeExceptionally(err);
              }
            );

            return future;
        }

    }

}
