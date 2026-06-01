/*
 * This file is part of Bits, licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2024-2026 ImBit
 *
 * Enjoy the Bits and Bobs :)
 */

package xyz.bitsquidd.bits.lifecycle.manager;

/**
 * Defines a template for service managers that respond to server lifecycle events.
 * <p>
 * Implementing classes can hook into various stages such as initial startup,
 * round-based initialisation/cleanup, and final shutdown sequences.
 *
 * @since 0.0.10
 */
public interface CoreManager {

    /**
     * Invoked during the global server startup phase.
     * <p>
     * This should be used for one-time setup tasks that must occur before any
     * game logic or command processing begins.
     *
     * @since 0.0.10
     */
    default void startup() {}

    /**
     * Invoked during the global server shutdown phase.
     * <p>
     * This should be used for releasing resources, saving data, and ensuring a
     * clean exit after all other activities have ceased.
     *
     * @since 0.0.10
     */
    default void shutdown() {}

    /**
     * Invoked at the beginning of a game round or significant lifecycle event.
     *
     * @since 0.0.10
     */
    @Deprecated(forRemoval = true, since = "0.0.18")
    default void initialise() {}

    /**
     * Invoked at the conclusion of a game round or significant lifecycle event.
     *
     * @since 0.0.10
     */
    @Deprecated(forRemoval = true, since = "0.0.18")
    default void cleanup() {}


}
