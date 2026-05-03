/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.exception;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

/**
 * Utility class for constructing Brigadier {@link com.mojang.brigadier.exceptions.CommandSyntaxException}s.
 *
 * @since 0.0.10
 */
public final class ExceptionBuilder {
    private ExceptionBuilder() {}

    /**
     * @param stringMessage the failure message to embed in the exception
     *
     * @return the constructed {@link CommandSyntaxException}
     *
     * @since 0.0.10
     */
    public static CommandSyntaxException createCommandException(String stringMessage) {
        Message message = toMessage(stringMessage);
        return new CommandSyntaxException(createExceptionType(message), message);
    }

    private static SimpleCommandExceptionType createExceptionType(Message message) {
        return new SimpleCommandExceptionType(message);
    }

    private static Message toMessage(String message) {
        return () -> message;
    }

}
