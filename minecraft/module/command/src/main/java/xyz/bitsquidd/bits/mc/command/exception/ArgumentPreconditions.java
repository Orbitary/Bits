/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.exception;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class containing precondition checks for command arguments.
 * <p>
 * Methods throw a {@link com.mojang.brigadier.exceptions.CommandSyntaxException}
 * if the precondition fails.
 *
 * @since 0.0.10
 */
public final class ArgumentPreconditions {
    private ArgumentPreconditions() {}

    /**
     * Checks if the provided string is neither null nor empty.
     *
     * @param string the string to check
     *
     * @throws CommandSyntaxException if the string is null or empty
     * @since 0.0.10
     */
    public static void checkNotEmpty(@Nullable String string) throws CommandSyntaxException {
        if (string == null || string.isEmpty()) {
            throw ExceptionBuilder.createCommandException("Input string cannot be empty.");
        }
    }


    /**
     * Checks if the provided object is not null.
     *
     * @param obj the object to check
     *
     * @throws CommandSyntaxException if the object is null
     * @since 0.0.10
     */
    public static void checkNotNull(@Nullable Object obj) throws CommandSyntaxException {
        if (obj == null) {
            throw ExceptionBuilder.createCommandException("Input cannot be null.");
        }
    }

}
