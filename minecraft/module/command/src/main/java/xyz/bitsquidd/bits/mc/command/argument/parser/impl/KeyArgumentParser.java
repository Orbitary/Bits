/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.key.Key;

import xyz.bitsquidd.bits.mc.command.argument.parser.DefaultArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.Collections;
import java.util.List;


/**
 * Argument parser for handling {@link Key Keys}.
 *
 * @since 0.0.20
 */

public final class KeyArgumentParser extends DefaultArgumentParser<Key> {
    public KeyArgumentParser() {
        super(TypeSignature.of(Key.class), "Key");
    }

    @Override
    public Key parse(String data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        return Key.key(data);
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        return ctx -> {
            String lastInput = ctx.getLastInput();
            if (lastInput.isEmpty()) return Collections.emptyList();
            if (lastInput.contains(Key.DEFAULT_SEPARATOR + "")) return Collections.emptyList();
            return List.of(
              lastInput + Key.DEFAULT_SEPARATOR,
              Key.MINECRAFT_NAMESPACE + Key.DEFAULT_SEPARATOR + lastInput
            );
        };
    }

}
