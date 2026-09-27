/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.wrapper.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ClassMap implements Iterable<Object> {
    private final Map<Class<?>, Object> map = new HashMap<>();

    public <T> void put(Class<T> clazz, T value) {
        map.put(clazz, value);
    }

    @SuppressWarnings("unchecked")
    public <T> void put(T value) {
        put((Class<T>)value.getClass(), value);
    }

    public <T> void putIfAbsent(Class<T> clazz, T value) {
        map.putIfAbsent(clazz, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Class<T> clazz) {
        return Optional.ofNullable((T)map.get(clazz));
    }

    public boolean contains(Class<?> clazz) {
        return map.containsKey(clazz);
    }


    @Override
    public Iterator<Object> iterator() {
        return map.values().iterator();
    }

}
