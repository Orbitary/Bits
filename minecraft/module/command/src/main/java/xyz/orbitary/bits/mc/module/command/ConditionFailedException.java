/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;


/**
 * Thrown when a command's {@link xyz.orbitary.bits.module.command.Condition} fails. The command does not run, and the
 * platform's error handler decides what the source sees.
 *
 * @since 0.1.6
 */
public final class ConditionFailedException extends RuntimeException {

    private final Class<?> condition;

    /**
     * @param condition the class of the condition that failed
     *
     * @since 0.1.6
     */
    public ConditionFailedException(Class<?> condition) {
        super("Condition failed: " + condition.getName(), null, false, false);
        this.condition = condition;
    }

    /**
     * @return the class of the condition that failed
     *
     * @since 0.1.6
     */
    public Class<?> condition() {
        return condition;
    }

}
