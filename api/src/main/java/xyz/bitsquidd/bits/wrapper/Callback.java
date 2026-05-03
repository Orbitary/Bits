/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper;

import xyz.bitsquidd.bits.config.Bits;

import java.util.ArrayList;
import java.util.List;


/**
 * A wrapper for executing code after an asynchronous event or action completes.
 * <p>
 * This serves as a lightweight alternative to {@link java.util.concurrent.CompletableFuture}
 * for internal library events.
 *
 * @since 0.0.10
 */
public final class Callback {
    private boolean completed = false;
    private final List<Runnable> listeners = new ArrayList<>();

    private Callback() {}

    /**
     * Creates a new, incomplete callback instance.
     *
     * @return a new callback
     *
     * @since 0.0.10
     */
    public static Callback create() {
        return new Callback();
    }

    /**
     * Creates a callback that is already in the completed state.
     * <p>
     * Any listeners registered subsequently will fire immediately.
     *
     * @return a completed callback
     *
     * @since 0.0.10
     */
    public static Callback completed() {
        Callback callback = new Callback();
        callback.complete();
        return callback;
    }

    /**
     * Creates a callback that will automatically complete after a specified delay.
     *
     * @param delay the delay in milliseconds before completion
     *
     * @return a callback scheduled for completion
     *
     * @since 0.0.10
     */
    public static Callback later(long delay) {
        Callback callback = new Callback();
        Bits.get().runLater(callback::complete, delay);
        return callback;
    }


    /**
     * Marks this callback as complete and triggers all registered listeners.
     * <p>
     * If the callback is already completed, this method has no effect.
     *
     * @since 0.0.10
     */
    public void complete() {
        if (completed) return;
        completed = true;
        listeners.forEach(Runnable::run);
    }

    /**
     * Checks whether this callback has transitioned to the completed state.
     *
     * @return true if completed, false otherwise
     *
     * @since 0.0.10
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Registers a listener to be executed when this callback enters the completed state.
     * <p>
     * If the callback is already completed, the listener is invoked immediately
     * on the current thread.
     *
     * @param listener the runnable to execute upon completion
     *
     * @return this callback instance for fluent chaining
     *
     * @since 0.0.10
     */
    public Callback whenComplete(Runnable listener) {
        if (completed) {
            listener.run();
        } else {
            listeners.add(listener);
        }
        return this;
    }

}