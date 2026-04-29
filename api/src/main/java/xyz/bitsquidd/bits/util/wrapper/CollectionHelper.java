/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.wrapper;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Provides static utility methods for manipulated and querying Java {@link java.util.Collection}s.
 *
 * @since 0.0.10
 */
public final class CollectionHelper {
    private CollectionHelper() {}

    /**
     * Updates an element at a specific index, automatically populating preceding indices
     * with a default value if the list needs to be expanded.
     *
     * @param <T>          the element type
     * @param list         the target list
     * @param index        the target index
     * @param element      the element to set
     * @param defaultValue a supplier for values used to fill gaps
     *
     * @return the modified list
     *
     * @since 0.0.10
     */
    public static <T> List<@Nullable T> setAndPopulate(List<@Nullable T> list, int index, T element, Supplier<@Nullable T> defaultValue) {
        while (list.size() <= index) {
            list.add(defaultValue.get());
        }
        list.set(index, element);
        return list;
    }

    /**
     * Safely retrieves an element from a list,
     * returning an empty {@link Optional} if the index is out of range.
     *
     * @param <T>   the element type
     * @param list  the list to query
     * @param index the target index
     *
     * @return an optional containing the element, or empty if out of bounds
     *
     * @since 0.0.10
     */
    public static <T> Optional<T> get(List<T> list, int index) {
        if (index < 0 || index >= list.size()) return Optional.empty();
        return Optional.of(list.get(index));
    }

    /**
     * Safely retrieves an element from a map,
     * returning an empty {@link Optional} if the key is not present or the value is null.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to query
     * @param key the key to look up
     *
     * @return an optional containing the value, or empty if the key is absent or maps to null
     *
     * @since 0.0.12
     */
    public static <K, V> Optional<V> get(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }


    /**
     * Updates an element at a specific index, automatically expanding the list
     * if necessary using a default value supplier.
     *
     * @param <T>          the element type
     * @param list         the target list
     * @param index        the target index, must be non-negative
     * @param element      the element to set
     * @param defaultValue a supplier for fill values
     *
     * @throws IndexOutOfBoundsException if index is negative
     * @since 0.0.10
     */
    public static <T> void set(List<T> list, int index, T element, Supplier<T> defaultValue) {
        if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
        while (list.size() <= index) {
            list.add(defaultValue.get());
        }
        list.set(index, element);
    }

    /**
     * Iterates over a collection and performs an action for each element, providing its index.
     *
     * @param <T>        the element type
     * @param collection the collection to iterate
     * @param consumer   the action to perform, receiving index and element
     *
     * @since 0.0.10
     */
    public static <T> void forEach(Collection<T> collection, BiConsumer<Integer, T> consumer) {
        List<T> list = List.copyOf(collection);
        for (int i = 0; i < collection.size(); i++) {
            consumer.accept(i, list.get(i));
        }
    }

    /**
     * Performs a cyclic shift of elements in a list.
     *
     * @param <T>         the element type
     * @param list        the list to modify
     * @param shiftAmount the number of positions to shift to the right
     *
     * @return the modified list
     *
     * @since 0.0.10
     */
    public static <T> List<T> shift(List<T> list, int shiftAmount) {
        if (list.isEmpty()) return list;

        int size = list.size();
        shiftAmount = ((shiftAmount % size) + size) % size; // Normalize shift amount

        if (shiftAmount == 0) return list;

        List<T> temp = List.copyOf(list);
        for (int i = 0; i < size; i++) {
            list.set((i + shiftAmount) % size, temp.get(i));
        }

        return list;
    }

}
