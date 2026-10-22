/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.event;

/**
 * An independently removable listener registration.
 *
 * @since 0.1.6
 */
public interface EventSubscription {
    /**
     * Removes the listener. Repeated calls have no effect.
     *
     * @since 0.1.6
     */
    void unsubscribe();

    /**
     * @return true until the listener is unsubscribed or expires
     *
     * @since 0.1.6
     */
    boolean active();

}
