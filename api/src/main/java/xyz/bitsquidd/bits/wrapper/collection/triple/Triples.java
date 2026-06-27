/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.triple;

import java.util.Objects;


/**
 * Internal helpers shared by {@link Triple} implementations so that equality stays consistent
 * regardless of which implementation produced an instance.
 *
 * @since 0.0.21
 */
final class Triples {
    private Triples() {}

    static boolean equals(Triple<?, ?, ?> triple, Object o) {
        if (triple == o) {
            return true;
        } else if (o instanceof Triple<?, ?, ?> other) {
            return Objects.equals(triple.getFirst(), other.getFirst())
              && Objects.equals(triple.getSecond(), other.getSecond())
              && Objects.equals(triple.getThird(), other.getThird());
        } else {
            return false;
        }
    }

    static int hashCode(Triple<?, ?, ?> triple) {
        return Objects.hash(triple.getFirst(), triple.getSecond(), triple.getThird());
    }

}