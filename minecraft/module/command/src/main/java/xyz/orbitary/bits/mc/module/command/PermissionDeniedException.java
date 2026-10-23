/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;


/**
 * Thrown when the source lacks the permission nodes a command requires. The command does not run, and the platform's
 * error handler decides what the source sees.
 *
 * @since 0.1.6
 */
public final class PermissionDeniedException extends RuntimeException {

    /**
     * @since 0.1.6
     */
    public PermissionDeniedException() {
        super("Missing permission", null, false, false);
    }

}
