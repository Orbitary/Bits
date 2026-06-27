/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.pair;

/**
 * A mutable container for holding two related objects.
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 *
 * @since 0.0.21
 */
public final class MutablePair<A, B> implements Pair<A, B> {
    private A first;
    private B second;

    public MutablePair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> MutablePair<A, B> of(A first, B second) {
        return new MutablePair<>(first, second);
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
     * Updates the first element of the pair.
     *
     * @param first the new value for the first element
     *
     * @return the previous value of the first element
     *
     * @since 0.0.21
     */
    public A setFirst(A first) {
        A old = this.first;
        this.first = first;
        return old;
    }

    /**
     * Updates the second element of the pair.
     *
     * @param second the new value for the second element
     *
     * @return the previous value of the second element
     *
     * @since 0.0.21
     */
    public B setSecond(B second) {
        B old = this.second;
        this.second = second;
        return old;
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
        return "MutablePair{first=" + first + ", second=" + second + '}';
    }

}