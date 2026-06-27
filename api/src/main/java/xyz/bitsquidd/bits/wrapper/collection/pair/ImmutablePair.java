/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.pair;

/**
 * An immutable container for holding two related objects.
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 *
 * @since 0.0.21
 */
public final class ImmutablePair<A, B> implements Pair<A, B> {
    private final A first;
    private final B second;

    private ImmutablePair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> ImmutablePair<A, B> of(A first, B second) {
        return new ImmutablePair<>(first, second);
    }

    @Override
    public A getFirst() {
        return this.first;
    }

    @Override
    public B getSecond() {
        return this.second;
    }

    /**
     * Returns a new pair with the first element replaced, leaving this instance unchanged.
     *
     * @since 0.0.21
     */
    public ImmutablePair<A, B> withFirst(A first) {
        return ImmutablePair.of(first, this.second);
    }

    /**
     * Returns a new pair with the second element replaced, leaving this instance unchanged.
     *
     * @since 0.0.21
     */
    public ImmutablePair<A, B> withSecond(B second) {
        return ImmutablePair.of(this.first, second);
    }

    @SuppressWarnings("EqualsDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return Pairs.equals(this, o);
    }

    @Override
    public int hashCode() {
        return Pairs.hashCode(this);
    }

    @Override
    public String toString() {
        return "ImmutablePair{first=" + first + ", second=" + second + '}';
    }

}