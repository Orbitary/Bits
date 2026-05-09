/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private @Nullable T value = null;

    /**
     * Initialises a new container with the given value.
     *
     * @param value the initial value, may be null
     *
     * @since 0.0.10
     */
    public Single(@Nullable T value) {
        this.value = value;
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
    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    /**
     * Updates the value held in this container.
     *
     * @param value the new value, may be null
     *
     * @since 0.0.10
     */
    public void set(@Nullable T value) {
        this.value = value;
    }

    public List<T> asList() {
        final T currentValue = value;
        return currentValue != null ? Collections.singletonList(currentValue) : Collections.emptyList();
    }

    public Set<T> asSet() {
        final T currentValue = value;
        return currentValue != null ? Collections.singleton(currentValue) : Collections.emptySet();
    }

    public void removeIf(Predicate<? super T> filter) {
        final T currentValue = value;
        if (currentValue != null && filter.test(currentValue)) this.value = null;
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

        final T currentValue = value;
        if (currentValue == null) return new Single<>();
        return new Single<>(mapper.apply(value));
    }


    @Override
    public String toString() {
        return "Single[" + value + ']';
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Single<?> other && Objects.equals(this.value, other.value));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

}
