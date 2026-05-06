/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.core;

import java.util.LinkedHashSet;
import java.util.Set;


public final class ClassHelper {
    private ClassHelper() {}

    public static Set<Class<?>> getAllSuperClasses(Class<?> clazz) {
        return getAllSuperClasses(clazz, Object.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<Class<? extends T>> getAllSuperClasses(Class<?> clazz, Class<T> bound) {
        Set<Class<? extends T>> types = new LinkedHashSet<>();
        if (clazz == null || clazz == Object.class || !bound.isAssignableFrom(clazz)) return types;

        types.add((Class<? extends T>)clazz);
        types.addAll(getAllSuperClasses(clazz.getSuperclass(), bound));

        for (Class<?> iface : clazz.getInterfaces()) {
            types.addAll(getAllSuperClasses(iface, bound));
        }

        return types;
    }

}
