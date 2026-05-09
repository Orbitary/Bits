/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util.bukkit.listener;

import org.bukkit.event.Listener;

import xyz.bitsquidd.bits.paper.lifecycle.manager.PaperManagerOrchestrator;


/**
 * A special {@link Listener Listener} that should never be cancelled.
 * By default all listeners should be cancelled on {@link PaperManagerOrchestrator#cleanup() PaperManagerContainer#cleanup()}.
 */
public interface PermanentListener extends Listener {
}
