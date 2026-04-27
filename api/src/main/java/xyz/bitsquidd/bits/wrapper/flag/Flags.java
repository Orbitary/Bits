/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.flag;

import java.util.Set;

/**
 * Utility class for working with sets of flags represented as bitmasks.
 *
 * @see Flag
 * @since 0.0.13
 */
public final class Flags {
    private Flags() {}

    public static int EMPTY = 0;

    @SafeVarargs
    public static <F extends Flag> int of(F... flags) {
        int mask = 0;
        for (F flag : flags) {
            mask |= flag.bit();
        }
        return mask;
    }

    public static <F extends Flag> int of(Set<? extends F> flags) {
        int mask = 0;
        for (F flag : flags) {
            mask |= flag.bit();
        }
        return mask;
    }

    public static <F extends Enum<F> & Flag> int all(Class<F> clazz) {
        int mask = 0;
        for (F flag : clazz.getEnumConstants()) {
            mask |= flag.bit();
        }
        return mask;
    }


    public static <F extends Flag> int add(int mask, F flag) {
        return mask | flag.bit();
    }

    public static <F extends Flag> int remove(int mask, F flag) {
        return mask & ~flag.bit();
    }

    public static <F extends Flag> int toggle(int mask, F flag) {
        return mask ^ flag.bit();
    }

    public static <F extends Flag> int set(int mask, F flag, boolean value) {
        return value ? add(mask, flag) : remove(mask, flag);
    }


    public static <F extends Flag> boolean isEmpty(int mask) {
        return mask == 0;
    }

    public static <F extends Flag> boolean has(int mask, F flag) {
        return flag.isIn(mask);
    }

}