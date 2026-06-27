/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper;

/**
 * A placeholder type used to represent an empty or absent value.
 *
 * @since 0.0.10
 */
public final class Empty {
    private Empty() {}

    /**
     * Returns a new empty instance.
     *
     * @return an empty instance
     *
     * @since 0.0.10
     */
    public static Empty empty() {
        return new Empty();
    }

}
