/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.argument;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import xyz.bitsquidd.bits.discord.command.JdaCommandContext;

import java.util.List;


/**
 * Maps a Java type to a Discord slash command {@link OptionType} and resolves
 * raw {@link OptionMapping} values back into the target type.
 *
 * @param <T> the resolved Java type
 */
public abstract class JdaArgumentParser<T> {

    /**
     * The Java type this parser handles.
     */
    public abstract Class<T> type();

    /**
     * The Discord option type this parser maps to.
     */
    public abstract OptionType optionType();

    /**
     * Resolves a raw Discord option into the target type.
     */
    public abstract T resolve(OptionMapping mapping, JdaCommandContext ctx);

    /**
     * Whether this parser supports dynamic autocomplete.
     */
    public boolean supportsAutocomplete() {
        return false;
    }

    /**
     * Returns autocomplete suggestions for the current user input.
     * Only called when {@link #supportsAutocomplete()} returns {@code true}.
     */
    public List<Command.Choice> autocomplete(String input) {
        return List.of();
    }

}
