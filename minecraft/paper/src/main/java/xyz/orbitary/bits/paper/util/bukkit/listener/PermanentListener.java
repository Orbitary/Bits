/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.util.bukkit.listener;

import org.bukkit.event.Listener;


/**
 * A {@link Listener} that remains registered during manager cleanup.
 * Bits unregisters it when its manager shuts down.
 */
public interface PermanentListener extends Listener {
}
