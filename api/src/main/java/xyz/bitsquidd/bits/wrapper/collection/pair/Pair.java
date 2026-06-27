/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.pair;

/**
 * Common contract for a generic container holding two related objects.
 *
 * <p>Implementations may be mutable or immutable; callers should not assume one or the other based on this
 * type alone. Two {@code Pair} instances are considered equal if both elements are equal, regardless of
 * which implementation produced them.</p>
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 *
 * @since 0.0.11
 */
public interface Pair<A, B> {

    /**
     * Creates a new immutable pair of objects.
     *
     * @param <A>    the type of the first object
     * @param <B>    the type of the second object
     * @param first  the first object, may be null
     * @param second the second object, may be null
     *
     * @return a new immutable pair containing the given objects
     *
     * @since 0.0.11
     */
    static <A, B> ImmutablePair<A, B> immutable(A first, B second) {
        return new ImmutablePair<>(first, second);
    }

    /**
     * Creates a new mutable pair of objects.
     *
     * @param <A>    the type of the first object
     * @param <B>    the type of the second object
     * @param first  the first object, may be null
     * @param second the second object, may be null
     *
     * @return a new mutable pair containing the given objects
     *
     * @since 0.0.11
     */
    static <A, B> MutablePair<A, B> mutable(A first, B second) {
        return new MutablePair<>(first, second);
    }

    /**
     * Gets the first element of the pair.
     *
     * @return the first element, may be null
     *
     * @since 0.0.11
     */
    A getFirst();

    /**
     * Gets the second element of the pair.
     *
     * @return the second element, may be null
     *
     * @since 0.0.11
     */
    B getSecond();

}