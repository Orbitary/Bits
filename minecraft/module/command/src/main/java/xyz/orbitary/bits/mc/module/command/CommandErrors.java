/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;


/**
 * The message a platform shows a source for a command that did not run.
 *
 * @since 0.1.6
 */
public final class CommandErrors {

    private CommandErrors() {}

    /**
     * @param error the error passed to the error handler
     *
     * @return a message describing the error for the source
     *
     * @since 0.1.6
     */
    public static String message(Throwable error) {
        if (error instanceof ConditionFailedException) return "You cannot use this command.";
        if (error instanceof CommandSyntaxException syntax && syntax.getMessage() != null) return syntax.getMessage();
        return "An error occurred while running this command.";
    }

}
