/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper;

/**
 * A wrapper for strings that should be parsed "greedily", typically consuming
 * the remainder of a command input including spaces.
 *
 * @since 0.0.10
 */
public final class GreedyString {
    public final String value;

    private GreedyString(String value) {
        this.value = value;
    }

    public static GreedyString of(String value) {
        return new GreedyString(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof GreedyString other) && other.value.equals(this.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
