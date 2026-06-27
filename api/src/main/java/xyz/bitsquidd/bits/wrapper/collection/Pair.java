/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection;

import java.util.Objects;


/**
 * A generic container for holding two related objects.
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 *
 * @since 0.0.10
 */
public final class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates a new pair of objects.
     *
     * @param <A>    the type of the first object
     * @param <B>    the type of the second object
     * @param first  the first object, may be null
     * @param second the second object, may be null
     *
     * @return a new pair containing the given objects
     *
     * @since 0.0.10
     */
    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }

    /**
     * Gets the first element of the pair.
     *
     * @return the first element, may be null
     *
     * @since 0.0.10
     */
    public A getFirst() {
        return this.first;
    }

    /**
     * Gets the second element of the pair.
     *
     * @return the second element, may be null
     *
     * @since 0.0.10
     */
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
     * @since 0.0.10
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
     * @since 0.0.10
     */
    public B setSecond(B second) {
        B old = this.second;
        this.second = second;
        return old;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Pair<?, ?> pair) {
            return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    @Override
    public String toString() {
        return "Pair{" +
          "first=" + first +
          ", second=" + second +
          '}';
    }

}
