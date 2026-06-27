/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection.triple;

/**
 * Common contract for a generic container holding three related objects.
 *
 * <p>Implementations may be mutable or immutable; callers should not assume one or the other based on this
 * type alone. Two {@code Triple} instances are considered equal if all three elements are equal, regardless
 * of which implementation produced them.</p>
 *
 * @param <A> the type of the first object
 * @param <B> the type of the second object
 * @param <C> the type of the third object
 *
 * @since 0.0.21
 */
public interface Triple<A, B, C> {

    /**
     * Creates a new immutable triple of objects.
     *
     * @param <A>    the type of the first object
     * @param <B>    the type of the second object
     * @param <C>    the type of the third object
     * @param first  the first object, may be null
     * @param second the second object, may be null
     * @param third  the third object, may be null
     *
     * @return a new immutable triple containing the given objects
     *
     * @since 0.0.21
     */
    static <A, B, C> Triple<A, B, C> immutable(A first, B second, C third) {
        return new ImmutableTriple<>(first, second, third);
    }

    /**
     * Creates a new mutable triple of objects.
     *
     * @param <A>    the type of the first object
     * @param <B>    the type of the second object
     * @param <C>    the type of the third object
     * @param first  the first object, may be null
     * @param second the second object, may be null
     * @param third  the third object, may be null
     *
     * @return a new mutable triple containing the given objects
     *
     * @since 0.0.21
     */
    static <A, B, C> Triple<A, B, C> mutable(A first, B second, C third) {
        return new MutableTriple<>(first, second, third);
    }


    /**
     * Gets the first element of the triple.
     *
     * @return the first element, may be null
     *
     * @since 0.0.21
     */
    A getFirst();

    /**
     * Gets the second element of the triple.
     *
     * @return the second element, may be null
     *
     * @since 0.0.21
     */
    B getSecond();

    /**
     * Gets the third element of the triple.
     *
     * @return the third element, may be null
     *
     * @since 0.0.21
     */
    C getThird();

}