/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.pair;

import java.util.Objects;


/**
 * Internal helpers shared by {@link Pair} implementations so that equality stays consistent
 * regardless of which implementation produced an instance.
 *
 * @since 0.0.21
 */
final class Pairs {
    private Pairs() {}

    static boolean equals(Pair<?, ?> pair, Object o) {
        if (pair == o) {
            return true;
        } else if (o instanceof Pair<?, ?> other) {
            return Objects.equals(pair.getFirst(), other.getFirst()) && Objects.equals(pair.getSecond(), other.getSecond());
        } else {
            return false;
        }
    }

    static int hashCode(Pair<?, ?> pair) {
        return Objects.hash(pair.getFirst(), pair.getSecond());
    }

}