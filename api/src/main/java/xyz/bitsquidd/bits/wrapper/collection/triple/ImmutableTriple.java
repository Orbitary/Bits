/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.triple;

/**
 * An immutable container for holding three related objects.
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 * @param <C> the type of the third object
 *
 * @since 0.0.21
 */
public final class ImmutableTriple<A, B, C> implements Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    public ImmutableTriple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }


    @Override
    public A getFirst() {
        return this.first;
    }

    @Override
    public B getSecond() {
        return this.second;
    }

    @Override
    public C getThird() {
        return this.third;
    }

    /**
     * Returns a new triple with the first element replaced, leaving this instance unchanged.
     *
     * @since 0.0.21
     */
    public ImmutableTriple<A, B, C> withFirst(A first) {
        return ImmutableTriple.of(first, this.second, this.third);
    }

    /**
     * Returns a new triple with the second element replaced, leaving this instance unchanged.
     *
     * @since 0.0.21
     */
    public ImmutableTriple<A, B, C> withSecond(B second) {
        return ImmutableTriple.of(this.first, second, this.third);
    }

    /**
     * Returns a new triple with the third element replaced, leaving this instance unchanged.
     *
     * @since 0.0.21
     */
    public ImmutableTriple<A, B, C> withThird(C third) {
        return ImmutableTriple.of(this.first, this.second, third);
    }

    @Override
    public boolean equals(Object o) {
        return Triples.equals(this, o);
    }

    @Override
    public int hashCode() {
        return Triples.hashCode(this);
    }

    @Override
    public String toString() {
        return "ImmutableTriple{first=" + first + ", second=" + second + ", third=" + third + '}';
    }

}