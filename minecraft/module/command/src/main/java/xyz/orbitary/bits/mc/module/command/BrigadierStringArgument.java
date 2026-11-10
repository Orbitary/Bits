/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import java.util.Collection;


/**
 * A {@link BrigadierArgument} typed in as one string, converted by {@link #parse}, and completed from {@link #values}.
 * <p>
 * Implement this on a concrete, top-level class that declares {@link #targetType()} to have it registered by the
 * reflection scanner. Declare the class generic in the command source, such as {@code Foo<S>}, to share it between
 * platforms.
 *
 * @param <S> the command source type
 * @param <T> the type the string is converted to
 *
 * @since 0.1.8
 */
public interface BrigadierStringArgument<S, T> extends BrigadierArgument<S> {

    /**
     * @param input the text that was typed
     *
     * @return the value, never {@code null}
     *
     * @throws CommandSyntaxException if the input is not valid, usually created with {@link BrigadierArgument#error}
     *
     * @since 0.1.8
     */
    T parse(String input) throws CommandSyntaxException;

    /**
     * Read on every completion request, so it may depend on state that does not exist when commands are registered.
     *
     * @return the values offered while typing
     *
     * @since 0.1.8
     */
    Collection<String> values();

    @Override
    default ArgumentType<?> argumentType() {
        return StringArgumentType.string();
    }

    @Override
    default Object read(CommandContext<S> context, String name) throws CommandSyntaxException {
        return parse(context.getArgument(name, String.class));
    }

    @Override
    default SuggestionProvider<S> suggestions() {
        return (context, builder) -> BrigadierArgument.<S>suggest(values()).getSuggestions(context, builder);
    }

}
