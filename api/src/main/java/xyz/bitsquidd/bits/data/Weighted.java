/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.data;

/**
 * Represents an object that has an associated weight value, which can be used for weighted random selection in {@link xyz.bitsquidd.bits.util.wrapper.CollectionHelper}.
 */
public interface Weighted {
    int weight();

}
