/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;


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
     * Creates an argument from an argument type and a function that reads its value.
     *
     * @param argumentType the Brigadier argument type that parses the input
     * @param reader       reads the parsed value from the context
     * @param <S>          the command source type
     *
     * @return the argument
     *
     * @since 0.1.6
     */
    static <S> BrigadierArgument<S> of(ArgumentType<?> argumentType, Reader<S> reader) {
        return new BrigadierArgument<>() {
            @Override
            public ArgumentType<?> argumentType() {
                return argumentType;
            }

            @Override
            public Object read(CommandContext<S> context, String name) throws CommandSyntaxException {
                return reader.read(context, name);
            }
        };
    }

    /**
     * Creates completions from fixed values, offering those that start with what has been typed so far.
     *
     * @param values the values to offer
     * @param <S>    the command source type
     *
     * @return a suggestion provider for an argument
     *
     * @since 0.1.6
     */
    static <S> SuggestionProvider<S> suggest(Collection<String> values) {
        return (context, builder) -> {
            String remaining = builder.getRemainingLowerCase();
            values.stream()
              .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(remaining))
              .forEach(builder::suggest);
            return builder.buildFuture();
        };
    }

    /**
     * Reads a parsed value from a command context.
     *
     * @param <S> the command source type
     *
     * @since 0.1.6
     */
    @FunctionalInterface
    interface Reader<S> {

        /**
         * @param context the context of the executing command
         * @param name    the argument name
         *
         * @return the value
         *
         * @throws CommandSyntaxException if the value cannot be resolved
         *
         * @since 0.1.6
         */
        Object read(CommandContext<S> context, String name) throws CommandSyntaxException;

    }

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
     * @throws CommandSyntaxException if the value cannot be resolved, with a message to send to the source
     *
     * @since 0.1.6
     */
    Object read(CommandContext<S> context, String name) throws CommandSyntaxException;

    /**
     * @return whether the argument takes the rest of the input, so it must be the last argument of its command
     *
     * @since 0.1.6
     */
    default boolean greedy() {
        return false;
    }

    /**
     * @return suggestions offered while typing, or {@code null} for none
     *
     * @since 0.1.6
     */
    default @Nullable SuggestionProvider<S> suggestions() {
        return null;
    }

}
