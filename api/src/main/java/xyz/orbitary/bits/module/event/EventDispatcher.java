/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.event;

/**
 * A cached dispatcher for one exact event type on one node, obtained from {@link EventNode#dispatcher(Class)}.
 * Hold it in a field to skip the per-call type lookup.
 *
 * @param <E> the exact event type
 *
 * @since 0.1.6
 */
public interface EventDispatcher<E extends Event> {
    /**
     * Dispatches the event to this node's listeners and its descendants.
     *
     * @param event the event, whose class must be exactly the dispatcher's type
     *
     * @return the same event, so a caller can check whether it was cancelled
     *
     * @throws IllegalArgumentException if the event's class is not the dispatcher's type
     * @since 0.1.6
     */
    E call(E event);

    /**
     * Allows callers to skip building events nobody listens to.
     *
     * @return true if this node or any descendant has a listener for the type
     *
     * @since 0.1.6
     */
    boolean hasListener();

}
