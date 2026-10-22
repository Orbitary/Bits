/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;
import xyz.orbitary.bits.module.command.Condition;


/**
 * A {@link Condition} for Discord slash commands. Its {@link Condition#test} runs before argument resolution.
 * <p>
 * When {@code test} returns {@code false}, the command is not run and {@link #denied} is sent as an ephemeral
 * reply (or a default message if {@code null}).
 * <p>
 * Guards that perform blocking operations (e.g. JDA {@code .complete()} calls) must be used on commands also
 * annotated with {@link xyz.orbitary.bits.module.command.Async @Async}.
 *
 * @since 0.1.6
 */
public interface JdaCommandGuard extends Condition<SlashCommandInteractionEvent> {

    /**
     * @return embed to send when {@link #test} returns {@code false}, or {@code null} for a default message
     *
     * @since 0.1.6
     */
    default @Nullable MessageEmbed denied() {
        return null;
    }

}
