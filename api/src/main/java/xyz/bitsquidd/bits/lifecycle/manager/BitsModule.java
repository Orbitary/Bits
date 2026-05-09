/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.lifecycle.manager;

import xyz.bitsquidd.bits.Bits;


/**
 * Common module definition for Bits.
 * <p>
 * Implementations are not automatically registered by {@link Bits}, you must register it in the {@code modules()} constructor of your platform implementation.
 *
 * @since 0.0.14
 */
public interface BitsModule extends CoreManager {
}
