/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.triple;

/**
 * A mutable container for holding three related objects.
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 * @param <C> the type of the third object
 *
 * @since 0.0.21
 */
public final class MutableTriple<A, B, C> implements Triple<A, B, C> {
    private A first;
    private B second;
    private C third;

    public MutableTriple(A first, B second, C third) {
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
     * Updates the first element of the triple.
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
     * Updates the second element of the triple.
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

    /**
     * Updates the third element of the triple.
     *
     * @param third the new value for the third element
     *
     * @return the previous value of the third element
     *
     * @since 0.0.21
     */
    public C setThird(C third) {
        C old = this.third;
        this.third = third;
        return old;
    }

    @SuppressWarnings("EqualsDoesntCheckParameterClass")
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
        return "MutableTriple{first=" + first + ", second=" + second + ", third=" + third + '}';
    }

}