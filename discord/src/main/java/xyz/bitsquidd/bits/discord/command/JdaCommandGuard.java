/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;


/**
 * Pre-execution check for a slash command. Implement and annotate the command class with
 * {@link xyz.bitsquidd.bits.discord.command.annotation.Guard @Guard} to enforce custom access rules.
 * <p>
 * Guards run before argument resolution. If {@link #check} returns {@code false}, command execution
 * is aborted and {@link #denied} is sent as an ephemeral reply (or a default message if {@code null}).
 * <p>
 * Guards that perform blocking operations (e.g. JDA {@code .complete()} calls) must be used on
 * commands also annotated with {@link xyz.bitsquidd.bits.discord.command.annotation.Async @Async}.
 */
public interface JdaCommandGuard {

    /**
     * @return {@code true} to allow execution, {@code false} to block it
     */
    boolean check(SlashCommandInteractionEvent event);

    /**
     * @return embed to send when {@link #check} returns {@code false}, or {@code null} for a default message
     */
    default @Nullable MessageEmbed denied() {
        return null;
    }

}
