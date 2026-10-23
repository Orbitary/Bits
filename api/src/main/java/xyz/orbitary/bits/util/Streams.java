/*
 * This file is part of the BiomeBattle project.
 *
 * Copyright (c) 2023-2026 BitSquidd - All Rights Reserved
 *
 * Unauthorized copying, distribution, or disclosure of this file, via any medium, is strictly prohibited.
 * Proprietary and confidential - for internal use only.
 */

package xyz.orbitary.bits.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public final class Streams {
    private Streams() {}

    public static <T> void forEachThenClear(Collection<T> collection, Consumer<T> action) {
        collection.forEach(action);
        collection.clear();
    }

    public static <T, K, V> Collector<T, ?, Map<K, V>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper);
    }

}
