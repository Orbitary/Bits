/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.lifecycle.manager;

import xyz.bitsquidd.bits.Bits;


/**
 * SPI marker for optional platform modules discovered via {@link java.util.ServiceLoader}.
 * <p>
 * Implementations are registered automatically by {@link Bits}
 * at construction time. Annotate implementations with {@code @AutoService(BitsModule.class)}.
 *
 * @since 0.0.14
 */
public interface BitsModule extends CoreManager {
}
