/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.lifecycle.manager;

import xyz.orbitary.bits.lifecycle.Subscription;
import xyz.orbitary.bits.util.Safety;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Subscriptions to manager lifecycle transitions across all containers and platforms.
 * @since 0.1.1
 */
public enum ManagerEvents {
    INSTANCE;

    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> preStartup = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> postStartup = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> preShutdown = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> postShutdown = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> preInitialise = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> postInitialise = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> preCleanup = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<? super CoreManager>> postCleanup = new CopyOnWriteArrayList<>();

    /**
     * Registers a callback before each manager starts.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @since 0.1.1
     */
    public Subscription preStartup(Consumer<? super CoreManager> callback) {
        return subscribe(preStartup, callback);
    }

    /**
     * Registers a callback after each manager's startup is attempted, even if it fails.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @since 0.1.1
     */
    public Subscription postStartup(Consumer<? super CoreManager> callback) {
        return subscribe(postStartup, callback);
    }

    /**
     * Registers a callback before each manager shuts down.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @since 0.1.1
     */
    public Subscription preShutdown(Consumer<? super CoreManager> callback) {
        return subscribe(preShutdown, callback);
    }

    /**
     * Registers a callback after each manager's shutdown is attempted, even if it fails.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @since 0.1.1
     */
    public Subscription postShutdown(Consumer<? super CoreManager> callback) {
        return subscribe(postShutdown, callback);
    }

    /**
     * Registers a callback before each manager initialises.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @deprecated As of 0.1.1, the initialise lifecycle is scheduled for removal.
     *             Use application-specific events instead.
     *
     * @since 0.1.1
     */
    @Deprecated(forRemoval = true, since = "0.1.1")
    public Subscription preInitialise(Consumer<? super CoreManager> callback) {
        return subscribe(preInitialise, callback);
    }

    /**
     * Registers a callback after each manager's initialisation is attempted, even if it fails.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @deprecated As of 0.1.1, the initialise lifecycle is scheduled for removal.
     *             Use application-specific events instead.
     *
     * @since 0.1.1
     */
    @Deprecated(forRemoval = true, since = "0.1.1")
    public Subscription postInitialise(Consumer<? super CoreManager> callback) {
        return subscribe(postInitialise, callback);
    }

    /**
     * Registers a callback before each manager cleans up.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @deprecated As of 0.1.1, the cleanup lifecycle is scheduled for removal.
     *             Use application-specific events instead.
     *
     * @since 0.1.1
     */
    @Deprecated(forRemoval = true, since = "0.1.1")
    public Subscription preCleanup(Consumer<? super CoreManager> callback) {
        return subscribe(preCleanup, callback);
    }

    /**
     * Registers a callback after each manager's cleanup is attempted, even if it fails.
     *
     * @param callback the action to run on the lifecycle thread
     *
     * @return an independent subscription that removes this callback when unsubscribed
     *
     * @throws NullPointerException if the callback is null
     *
     * @deprecated As of 0.1.1, the cleanup lifecycle is scheduled for removal.
     *             Use application-specific events instead.
     *
     * @since 0.1.1
     */
    @Deprecated(forRemoval = true, since = "0.1.1")
    public Subscription postCleanup(Consumer<? super CoreManager> callback) {
        return subscribe(postCleanup, callback);
    }

    void dispatchPreInitialise(CoreManager manager) {
        dispatch(preInitialise, manager, "preInitialise");
    }

    void dispatchPostInitialise(CoreManager manager) {
        dispatch(postInitialise, manager, "postInitialise");
    }

    void dispatchPreCleanup(CoreManager manager) {
        dispatch(preCleanup, manager, "preCleanup");
    }

    void dispatchPostCleanup(CoreManager manager) {
        dispatch(postCleanup, manager, "postCleanup");
    }

    void dispatchPreStartup(CoreManager manager) {
        dispatch(preStartup, manager, "preStartup");
    }

    void dispatchPostStartup(CoreManager manager) {
        dispatch(postStartup, manager, "postStartup");
    }

    void dispatchPreShutdown(CoreManager manager) {
        dispatch(preShutdown, manager, "preShutdown");
    }

    void dispatchPostShutdown(CoreManager manager) {
        dispatch(postShutdown, manager, "postShutdown");
    }

    private static void dispatch(
      CopyOnWriteArrayList<? extends Consumer<? super CoreManager>> callbacks,
      CoreManager manager,
      String phase
    ) {
        callbacks.forEach(callback -> Safety.safeExecute(
            manager.getClass().getSimpleName() + " " + phase + " subscriber",
            () -> callback.accept(manager)
        ));
    }

    private static Subscription subscribe(
      CopyOnWriteArrayList<? super Consumer<? super CoreManager>> callbacks,
      Consumer<? super CoreManager> callback
    ) {
        Objects.requireNonNull(callback, "callback");
        Consumer<CoreManager> registration = callback::accept;
        callbacks.add(registration);
        return () -> callbacks.remove(registration);
    }
}
