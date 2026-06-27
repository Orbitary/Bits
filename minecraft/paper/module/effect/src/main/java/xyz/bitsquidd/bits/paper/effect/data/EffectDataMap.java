/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


public final class EffectDataMap {
    private final Map<EffectData<?>, Object> directValues = new HashMap<>();             // The values stored directly in this map.
    private final Map<EffectData<?>, List<Object>> rawValues = new HashMap<>();          // The values stored in this map from children, without the child's children.
    private final Map<EffectData<?>, List<Object>> directChildValues = new HashMap<>();  // The values stored in this map from children, including the child's children.


    public <D> void put(EffectData<D> effectData, D value) {
        directValues.put(effectData, value);
        rawValues.computeIfAbsent(effectData, _ -> new ArrayList<>()).add(value);
    }

    @SuppressWarnings("unchecked")
    public <D> Optional<D> get(EffectData<D> effectData) {
        return Optional.ofNullable((D)directValues.get(effectData));
    }

    @SuppressWarnings("unchecked")
    public <D> List<D> getRaw(EffectData<D> effectData) {
        return (List<D>)rawValues.getOrDefault(effectData, List.of());
    }


    @SuppressWarnings("unchecked")
    public void mergeFrom(List<EffectDataMap> children) {
        Set<EffectData<?>> allKeys = new HashSet<>(directValues.keySet());
        children.forEach(child -> allKeys.addAll(child.directValues.keySet()));

        for (EffectData<?> key : allKeys) {
            Optional<Object> parentValue = Optional.ofNullable(directValues.get(key));

            List<Object> childValues = children.stream()
              .map(child -> child.directValues.get(key))
              .filter(Objects::nonNull)
              .toList();

            if (parentValue.isPresent() || !childValues.isEmpty()) {
                Object mergedValue = ((EffectData<Object>)key).mergeStrategy(parentValue, childValues);
                this.directValues.put(key, mergedValue);
            }
        }

        children.forEach(child -> {
            child.directValues.forEach((key, value) -> this.directChildValues.computeIfAbsent(key, _ -> new ArrayList<>()).add(value));
            child.rawValues.forEach((key, values) -> this.rawValues.computeIfAbsent(key, _ -> new ArrayList<>()).addAll(values));
        });
    }


}
