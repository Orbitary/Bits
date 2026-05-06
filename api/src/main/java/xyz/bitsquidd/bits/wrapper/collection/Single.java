/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * A mutable container that holds a single, potentially nullable value.
 *
 * @param <T> the type of value being wrapped
 *
 * @since 0.0.10
 */
public final class Single<T> {

    /**
     * The wrapped value. May be {@code null}.
     */
    private final Set<T> value = new HashSet<>(1);

    /**
     * Initialises a new container with the given value.
     *
     * @param value the initial value, may be null
     *
     * @since 0.0.10
     */
    public Single(T value) {
        this.value.add(value);
    }

    public Single() {}


    /**
     * Creates a new container holding a non-null value.
     *
     * @param <T>   the type of the value
     * @param value the value to wrap, must not be null
     *
     * @return a new container holding the value
     *
     * @throws NullPointerException if the value is null
     * @since 0.0.10
     */
    public static <T> Single<T> of(T value) {
        return new Single<>(Objects.requireNonNull(value, "value"));
    }

    /**
     * Creates a new container with no initial value.
     *
     * @param <T> the type of the value
     *
     * @return a new empty container
     *
     * @since 0.0.10
     */
    public static <T> Single<T> empty() {
        return new Single<>();
    }

    /**
     * Retrieves the current value held in this container.
     *
     * @return the current value, may be null
     *
     * @since 0.0.10
     */
    public @Nullable T get() {
        return value.isEmpty() ? null : value.iterator().next();
    }

    /**
     * Updates the value held in this container.
     *
     * @param value the new value, may be null
     *
     * @since 0.0.10
     */
    public void set(T value) {
        this.value.clear();
        this.value.add(value);
    }

    /**
     * Checks if this container currently holds a non-null value.
     *
     * @return true if a value is present, false otherwise
     *
     * @since 0.0.10
     */
    public boolean isPresent() {
        return !value.isEmpty();
    }

    /**
     * Resets the held value to null.
     *
     * @since 0.0.10
     */
    public void clear() {
        this.value.clear();
    }


    public List<T> asList() {
        return isPresent() ? List.of(get()) : Collections.emptyList();
    }

    public void removeIf(Predicate<? super T> filter) {
        if (isPresent() && filter.test(get())) clear();
    }


    /**
     * Transforms the current value and returns a new container with the result.
     * <p>
     * If the current value is null, the resulting container will also be empty.
     *
     * @param <R>    the type of the transformed value
     * @param mapper the function to apply to the value
     *
     * @return a new container holding the mapped value
     *
     * @since 0.0.10
     */
    public <R> Single<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        if (!isPresent()) return new Single<>();
        return new Single<>(mapper.apply(get()));
    }


    @Override
    public String toString() {
        return "Single[" + value + ']';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Single<?> other)) return false;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

}
