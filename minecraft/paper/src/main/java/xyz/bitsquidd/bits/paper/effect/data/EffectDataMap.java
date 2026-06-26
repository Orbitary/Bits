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
    private final Map<EffectData<?>, Object> merged = new HashMap<>();
    private final Map<EffectData<?>, List<Object>> raw = new HashMap<>();


    public <D> void put(EffectData<D> effectData, D value) {
        merged.put(effectData, value);
        raw.computeIfAbsent(effectData, _ -> new ArrayList<>()).add(value);
    }

    @SuppressWarnings("unchecked")
    public <D> Optional<D> get(EffectData<D> effectData) {
        return Optional.ofNullable((D)merged.get(effectData));
    }

    @SuppressWarnings("unchecked")
    public <D> List<D> getRaw(EffectData<D> effectData) {
        return (List<D>)raw.getOrDefault(effectData, List.of());
    }


    @SuppressWarnings("unchecked")
    public void mergeFrom(List<EffectDataMap> children) {
        Set<EffectData<?>> allKeys = new HashSet<>(merged.keySet());
        children.forEach(child -> allKeys.addAll(child.merged.keySet()));

        for (EffectData<?> key : allKeys) {
            Optional<Object> parentValue = Optional.ofNullable(merged.get(key));

            List<Object> childValues = children.stream()
              .map(child -> child.merged.get(key))
              .filter(Objects::nonNull)
              .toList();

            if (parentValue.isPresent() || !childValues.isEmpty()) {
                Object mergedValue = ((EffectData<Object>)key).mergeStrategy(parentValue, childValues);
                this.merged.put(key, mergedValue);
            }
        }

        children.forEach(child -> child.raw.forEach((key, values) -> this.raw.computeIfAbsent(key, _ -> new ArrayList<>()).addAll(values)));
    }


}
