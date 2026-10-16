/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.lifecycle;

/**
 * An independently removable event registration.
 */
@FunctionalInterface
public interface Subscription {
    /**
     * Removes this registration. Repeated calls have no effect.
     *
     * @since 0.1.1
     */
    void unsubscribe();
}
