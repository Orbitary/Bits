/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util;

import xyz.bitsquidd.bits.log.Logger;

import java.util.function.Supplier;

/**
 * Provides static utilities for exception-safe task execution and error reporting.
 *
 * @since 0.0.10
 */
public final class Safety {
    private Safety() {}

    /**
     * Executes a runnable task, swallowing and logging any exceptions that occur.
     *
     * @param task the task to execute
     *
     * @since 0.0.10
     */
    public static void safeExecute(final Runnable task) {
        safeExecute("unnamed", task);
    }

    /**
     * Executes a named runnable task, swallowing and logging any exceptions that occur.
     *
     * @param name the descriptive name of the task
     * @param task the task to execute
     *
     * @since 0.0.10
     */
    public static void safeExecute(String name, final Runnable task) {
        try {
            task.run();
        } catch (final Exception e) {
            Logger.exception("Error during " + name + " task execution.", e);
        }
    }

    /**
     * Executes a supplier and returns its result, or a default value if an exception is thrown.
     *
     * @param <T>          the type of value being returned
     * @param task         the result supplier
     * @param defaultValue the value to return on failure
     *
     * @since 0.0.10
     */
    public static <T> T safeExecute(final Supplier<T> task, T defaultValue) {
        return safeExecute("unnamed", task, defaultValue);
    }

    /**
     * Executes a named supplier and returns its result, or a default value if an exception is thrown.
     *
     * @param <T>          the type of value being returned
     * @param name         the descriptive name of the task
     * @param task         the result supplier
     * @param defaultValue the value to return on failure
     *
     * @since 0.0.10
     */
    public static <T> T safeExecute(String name, final Supplier<T> task, T defaultValue) {
        try {
            return task.get();
        } catch (final Exception e) {
            Logger.exception("Error during " + name + " task execution.", e);
            return defaultValue;
        }
    }

}
