/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;


/**
 * Wrapper commonly used for builders - which returns the set on add()
 *
 * @param <K> the type of the map key
 * @param <V> the type of the map value
 */
public class AddableMap<K, V> implements Buildable<Map<K, V>> {
    private final Map<K, V> map = new HashMap<>();

    @Override
    public final Map<K, V> build() {
        return Map.copyOf(map);
    }

    public final AddableMap<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public final AddableMap<K, V> putAll(Map<? extends K, ? extends V> element) {
        for (Map.Entry<? extends K, ? extends V> e : element.entrySet()) {
            put(e.getKey(), e.getValue());
        }
        return this;
    }

    public final Collector<Map.Entry<K, V>, ?, AddableMap<K, V>> toCollector() {
        return Collector.of(
          AddableMap::empty,
          (map, entry) -> map.put(entry.getKey(), entry.getValue()),
          (left, right) -> left.putAll(right.build())
        );
    }


    public static <K, V> AddableMap<K, V> of(Map<? extends K, ? extends V> map) {
        return new AddableMap<K, V>().putAll(map);
    }


    public static <K, V> AddableMap<K, V> empty() {
        return new AddableMap<>();
    }


}
