/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.velocity.util.velocity.listener;

import com.velocitypowered.api.event.EventManager;

import xyz.bitsquidd.bits.config.BitsVelocity;

import java.util.Collection;


/**
 * Utility class for managing Velocity event listeners.
 * <p>
 * See <a href="https://docs.papermc.io/velocity/dev/event-api/">Working with events</a> for more information.
 */
public final class Listeners {
    private static final Object plugin = BitsVelocity.get().getPlugin();
    private static final EventManager eventManager = BitsVelocity.get().getServer().getEventManager();

    private Listeners() {}

    public static void register(final Object listener) {
        eventManager.register(plugin, listener);
    }

    public static void unregister(final Object listener) {
        eventManager.unregisterListener(plugin, listener);
    }

    public static void unregister(final Collection<Object> listeners) {
        listeners.forEach(Listeners::unregister);
    }

}
