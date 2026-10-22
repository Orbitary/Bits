/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

/**
 * A check run before a command, such as a permission test. Implementations are referenced by {@link Guard}.
 *
 * @param <A> the actor type the platform passes to the command
 *
 * @since 0.1.6
 */
@FunctionalInterface
public interface Condition<A> {

    /**
     * @param actor the actor invoking the command
     *
     * @return true if the command may run
     *
     * @since 0.1.6
     */
    boolean test(A actor);

}
