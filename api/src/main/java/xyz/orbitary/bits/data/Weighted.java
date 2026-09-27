/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.data;

import xyz.orbitary.bits.util.wrapper.CollectionHelper;

/**
 * Represents an object that has an associated weight value, which can be used for weighted random selection in {@link CollectionHelper}.
 */
public interface Weighted {
    int weight();

}
