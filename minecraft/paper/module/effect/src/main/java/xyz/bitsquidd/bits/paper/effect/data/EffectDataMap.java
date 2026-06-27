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


/**
 * Stores typed metadata for an effect, keyed by {@link EffectData}.
 * <p>
 * After values are added via {@link #put}, child maps are merged in via {@link #mergeFrom},
 * which resolves the final value for each key using {@link EffectData#mergeStrategy}.
 *
 * @since 0.0.21
 */
public final class EffectDataMap {
    private final Map<EffectData<?>, Object> directValues = new HashMap<>();             // The values stored directly in this map.
    private final Map<EffectData<?>, List<Object>> rawValues = new HashMap<>();          // The values stored in this map from children, without the child's children.


    /**
     * Returns true if this map contains a value for the given key.
     *
     * @param effectData the key to look up
     *
     * @since 0.0.21
     */
    public boolean containsKey(EffectData<?> effectData) {
        return directValues.containsKey(effectData);
    }


    /**
     * Associates a value with the given key in this map.
     *
     * @param effectData the key
     * @param value      the value to store
     *
     * @since 0.0.21
     */
    public <D> void put(EffectData<D> effectData, D value) {
        directValues.put(effectData, value);
        rawValues.computeIfAbsent(effectData, _ -> new ArrayList<>()).add(value);
    }

    /**
     * Merges a singleton value with the given key into this map, using the key's {@link EffectData#mergeStrategy} to resolve the final value.
     *
     * @param effectData the key
     * @param value      the value to merge
     *
     * @since 0.0.21
     */
    @SuppressWarnings("unchecked")
    public <D> void merge(EffectData<D> effectData, D value) {
        rawValues.computeIfAbsent(effectData, _ -> new ArrayList<>()).add(value);
        List<D> raw = (List<D>)rawValues.get(effectData);
        directValues.put(effectData, effectData.mergeStrategy(Optional.empty(), raw));
    }


    /**
     * Returns the resolved value for the given key, or empty if not present.
     *
     * @param effectData the key to look up
     *
     * @return the resolved value, or empty
     *
     * @since 0.0.21
     */
    @SuppressWarnings("unchecked")
    public <D> Optional<D> get(EffectData<D> effectData) {
        return Optional.ofNullable((D)directValues.get(effectData));
    }

    /**
     * Returns all raw (unmerged) values for the given key from this map and any merged
     * children, in insertion order.
     *
     * @param effectData the key to look up
     *
     * @return an unmodifiable list of raw values, or an empty list if none are present
     *
     * @since 0.0.21
     */
    @SuppressWarnings("unchecked")
    public <D> List<D> getRaw(EffectData<D> effectData) {
        return (List<D>)rawValues.getOrDefault(effectData, List.of());
    }


    /**
     * Merges data from the given child maps into this map. Resolved values are computed
     * using each key's {@link EffectData#mergeStrategy}.
     *
     * @param children the child maps to merge
     *
     * @since 0.0.21
     */
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
            child.rawValues.forEach((key, values) -> this.rawValues.computeIfAbsent(key, _ -> new ArrayList<>()).addAll(values));
        });
    }


}
