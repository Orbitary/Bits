/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.exception;

import xyz.bitsquidd.bits.mc.command.annotation.Command;


/**
 * Thrown to indicate an error during the construction or registration of a command tree.
 * <p>
 * This exception usually indicates that a command is missing required annotations like
 * {@link Command}, or that its parameters cannot be reliably
 * mapped to recognized Brigadier argument types.
 * <p>
 * Example usage:
 * <pre>{@code
 * if (commandAnnotation == null) {
 *     throw new CommandBuildException("Class " + commandClass + " must be annotated with @Command");
 * }
 * }</pre>
 *
 * @since 0.0.10
 */
public class CommandBuildException extends RuntimeException {
    public CommandBuildException(String message) {
        super(message);
    }

    public CommandBuildException(String message, Throwable cause) {
        super(message, cause);
    }

}
