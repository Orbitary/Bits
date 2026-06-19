/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.parser.DefaultArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * Argument parser for standard {@link java.util.UUID}s.
 * <p>
 * Automatically sanitises string inputs by stripping non-hexadecimal characters before attempting
 * to parse into canonical UUID representation.
 * <p>
 * Example internal mapping:
 * <pre>{@code
 * manager.getArgumentRegistry().getParser(TypeSignature.of(UUID.class));
 * }</pre>
 *
 * @since 0.0.10
 */

public final class UUIDArgumentParser extends DefaultArgumentParser<UUID> {
    private static final List<Integer> DASH_POSITIONS = List.of(8, 12, 16, 20);
    private static final char DASH_CHAR = '-';
    private static final int HEX_CHAR_COUNT = 32;
    private static final int CANONICAL_LENGTH = HEX_CHAR_COUNT + DASH_POSITIONS.size();

    public UUIDArgumentParser() {
        super(TypeSignature.of(UUID.class), "UUID");
    }

    @Override
    public UUID parse(String data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        String hexOnly = data.replaceAll("[^a-fA-F0-9]", "");
        if (hexOnly.length() != HEX_CHAR_COUNT) throw ExceptionBuilder.createCommandException("Invalid UUID format: " + data);

        StringBuilder canonical = new StringBuilder(CANONICAL_LENGTH);
        for (int i = 0; i < hexOnly.length(); i++) {
            if (DASH_POSITIONS.contains(i)) canonical.append(DASH_CHAR);
            canonical.append(hexOnly.charAt(i));
        }

        try {
            return UUID.fromString(canonical.toString());
        } catch (IllegalArgumentException e) {
            throw ExceptionBuilder.createCommandException("Invalid UUID format: " + data);
        }
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        return ctx -> {
            String lastInput = ctx.getLastInput();
            if (lastInput.isEmpty()) return Collections.emptyList();
            if (lastInput.length() >= CANONICAL_LENGTH) return Collections.emptyList();

            String hexOnly = lastInput.replaceAll("[^a-fA-F0-9]", "");
            if (hexOnly.length() > HEX_CHAR_COUNT) return Collections.emptyList();

            StringBuilder suggestion = new StringBuilder(lastInput);
            for (int pos : DASH_POSITIONS) {
                if (suggestion.length() > pos && suggestion.charAt(pos) != DASH_CHAR) {
                    suggestion.insert(pos, DASH_CHAR);
                }
            }
            return List.of(suggestion.toString());
        };
    }

}
