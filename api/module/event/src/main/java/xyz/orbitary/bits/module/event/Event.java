/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.event;

import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;

/**
 * Base class for anything dispatched through an {@link EventNode}.
 * <p>
 * A listener denies an event by {@linkplain #cancel(String) cancelling} it. Cancelling is one-way: once cancelled, an
 * event stays cancelled.
 *
 * @since 0.1.6
 */
public abstract class Event {
    private static final VarHandle CANCEL_REASON;

    static {
        try {
            CANCEL_REASON = MethodHandles.lookup().findVarHandle(Event.class, "cancelReason", String.class);
        } catch (final ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private volatile @Nullable String cancelReason;

    /**
     * @return true if a listener has cancelled the event
     *
     * @since 0.1.6
     */
    public final boolean cancelled() {
        return cancelReason != null;
    }

    /**
     * @return why the event was cancelled, or null if it was not
     *
     * @since 0.1.6
     */
    public final @Nullable String cancelReason() {
        return cancelReason;
    }

    /**
     * Cancels the event. If it is already cancelled, the first reason is kept.
     *
     * @param reason a description of why the event was cancelled
     *
     * @since 0.1.6
     */
    public final void cancel(final String reason) {
        CANCEL_REASON.compareAndSet(this, (String)null, Objects.requireNonNull(reason, "reason"));
    }

}
