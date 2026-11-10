/*
 * This file is part of the BiomeBattle project.
 *
 * Copyright (c) 2023-2026 BitSquidd - All Rights Reserved
 *
 * Unauthorized copying, distribution, or disclosure of this file, via any medium, is strictly prohibited.
 * Proprietary and confidential - for internal use only.
 */

package xyz.orbitary.bits.paper.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.kyori.adventure.key.Key;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;

import java.util.Map;
import java.util.Optional;

import static xyz.orbitary.bits.paper.util.Keys.NSK;
import static xyz.orbitary.bits.util.serializer.SerializationManager.SERIALIZER;


public final class PersistentDataHelper {
    private static final Map<Class<?>, PersistentDataType<?, ?>> NATIVE_TYPES = Map.ofEntries(
      Map.entry(byte.class, PersistentDataType.BYTE),
      Map.entry(short.class, PersistentDataType.SHORT),
      Map.entry(int.class, PersistentDataType.INTEGER),
      Map.entry(long.class, PersistentDataType.LONG),
      Map.entry(float.class, PersistentDataType.FLOAT),
      Map.entry(double.class, PersistentDataType.DOUBLE),
      Map.entry(boolean.class, PersistentDataType.BOOLEAN),
      Map.entry(String.class, PersistentDataType.STRING),
      Map.entry(byte[].class, PersistentDataType.BYTE_ARRAY),
      Map.entry(int[].class, PersistentDataType.INTEGER_ARRAY),
      Map.entry(long[].class, PersistentDataType.LONG_ARRAY),
      Map.entry(PersistentDataContainer.class, PersistentDataType.TAG_CONTAINER)
    );

    private PersistentDataHelper() {}

    @SuppressWarnings("unchecked")
    public static <T> void set(PersistentDataHolder holder, Key key, T value) {
        PersistentDataType<?, T> type = toNative((Class<T>)value.getClass());
        if (type != null) {
            holder.getPersistentDataContainer().set(NSK(key), type, value);
            return;
        }

        try {
            holder.getPersistentDataContainer().set(NSK(key), PersistentDataType.STRING, SERIALIZER.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            Logger.warn("Failed to serialize value for key " + key + ": " + e.getMessage());
        }
    }

    public static <T> Optional<T> get(PersistentDataHolder holder, Key key, Class<T> clazz) {
        PersistentDataType<?, T> type = toNative(clazz);
        if (type != null) return Optional.ofNullable(holder.getPersistentDataContainer().get(NSK(key), type));

        String stringValue = holder.getPersistentDataContainer().get(NSK(key), PersistentDataType.STRING);
        if (stringValue == null) return Optional.empty();

        try {
            return Optional.of(SERIALIZER.readValue(stringValue, clazz));
        } catch (JsonProcessingException e) {
            Logger.warn("Failed to deserialize value for key " + key + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void remove(PersistentDataHolder holder, Key key) {
        holder.getPersistentDataContainer().remove(NSK(key));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(PersistentDataHolder holder, Key key, T defaultValue) {
        return get(holder, key, defaultValue.getClass()).map(v -> (T)v).orElse(defaultValue);
    }

    public static boolean has(PersistentDataHolder holder, Key key) {
        return holder.getPersistentDataContainer().has(NSK(key));
    }

    public static void tag(PersistentDataHolder holder, Key tag) {
        set(holder, tag, true);
    }


    @SuppressWarnings("unchecked")
    private static <T> @Nullable PersistentDataType<?, T> toNative(Class<T> value) {
        return (PersistentDataType<?, T>)NATIVE_TYPES.get(ReflectionUtils.Primitive.toOrSelf(value));
    }

}
