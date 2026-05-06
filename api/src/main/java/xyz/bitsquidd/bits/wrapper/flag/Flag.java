/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.flag;

/**
 * Interface for enums that represent individual flags in a bitmask.
 *
 * @see Flags
 * @since 0.0.13
 */
public interface Flag {
    int bit();

    default boolean isIn(int mask) {
        return (mask & bit()) != 0;
    }

}