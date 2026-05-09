/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

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
@ArgumentParser
public final class UUIDArgumentParser extends AbstractArgumentParser<UUID> {

    public UUIDArgumentParser() {
        super(TypeSignature.of(UUID.class), "UUID");
    }

    @Override
    public UUID parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        String inputString = singletonInputValidation(inputObjects, String.class);

        String hexOnly = inputString.replaceAll("[^a-fA-F0-9]", "");

        if (hexOnly.length() != 32) throw ExceptionBuilder.createCommandException("Invalid UUID format: " + inputString);

        String canonical = hexOnly.substring(0, 8) + "-"
          + hexOnly.substring(8, 12) + "-"
          + hexOnly.substring(12, 16) + "-"
          + hexOnly.substring(16, 20) + "-"
          + hexOnly.substring(20);

        try {
            return UUID.fromString(canonical);
        } catch (IllegalArgumentException e) {
            throw ExceptionBuilder.createCommandException("Invalid UUID format: " + inputString);
        }
    }

}
