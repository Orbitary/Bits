/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.Nullable;


/**
 * Maps one Java type to a Brigadier argument: the argument type that parses input, and how the parsed value is
 * read back from the command context.
 *
 * @param <S> the command source type
 *
 * @since 0.1.6
 */
public interface BrigadierArgument<S> {

    /**
     * @return the Brigadier argument type that parses the input
     *
     * @since 0.1.6
     */
    ArgumentType<?> argumentType();

    /**
     * @param context the context of the executing command
     * @param name    the argument name
     *
     * @return the parsed value, in the type this argument maps to
     *
     * @since 0.1.6
     */
    Object read(CommandContext<S> context, String name);

    /**
     * @return suggestions offered while typing, or {@code null} for none
     *
     * @since 0.1.6
     */
    default @Nullable SuggestionProvider<S> suggestions() {
        return null;
    }

}
