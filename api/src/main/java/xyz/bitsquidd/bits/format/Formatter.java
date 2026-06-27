/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.format;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A utility class for converting diverse object types into human-readable string representations.
 * <p>
 * This class supports a registry of custom formatter functions, allowing for
 * specialized string conversion logic for specific classes (e.g., Dates, UUIDs).
 *
 * @since 0.0.10
 */
public final class Formatter {
    private static final Map<Class<?>, FormatterFunction<?>> formatters = new ConcurrentHashMap<>();

    /**
     * A functional interface for defining how an object of a specific type should be formatted.
     *
     * @param <T> the type of object to format
     *
     * @since 0.0.10
     */
    @FunctionalInterface
    public interface FormatterFunction<T> {
        /**
         * Converts the given object into a string.
         *
         * @param obj the object to format
         *
         * @return the formatted string
         *
         * @since 0.0.10
         */
        String apply(T obj);

    }

    private Formatter() {}

    /**
     * Registers a custom formatter function for a specific class.
     *
     * @param <T>       the type of object being formatted
     * @param clazz     the class to associate with this formatter
     * @param formatter the function to use for formatting
     *
     * @since 0.0.10
     */
    public static <T> void registerFormatter(Class<T> clazz, FormatterFunction<? super T> formatter) {
        formatters.put(clazz, formatter);
    }

    /**
     * Formats the given object into a string using the best available formatter.
     * <p>
     * If a specific formatter is registered for the object's class or a parent
     * class, it will be used. Otherwise, {@link Object#toString()} is called.
     *
     * @param obj the object to format, may be null
     *
     * @return the formatted string, or "null" if the input is null
     *
     * @since 0.0.10
     */
    @SuppressWarnings("unchecked")
    public static String format(@Nullable Object obj) {
        if (obj == null) return "null";

        for (Map.Entry<Class<?>, FormatterFunction<?>> entry : formatters.entrySet()) {
            if (entry.getKey().isAssignableFrom(obj.getClass())) {
                return ((FormatterFunction<Object>)entry.getValue()).apply(obj);
            }
        }

        return obj.toString();
    }

    public static final FormatterFunction<Date> DATE_FORMATTER = date -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

    public static final FormatterFunction<UUID> UUID_FORMATTER = UUID::toString;

    public static final FormatterFunction<Map<?, ?>> MAP_FORMATTER = map -> {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(format(entry.getKey())).append(": ").append(format(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    };

    public static final FormatterFunction<Collection<?>> COLLECTION_FORMATTER = collection -> {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(format(item));
        }
        sb.append("]");
        return sb.toString();
    };

    static {
//        registerFormatter(Collection.class, COLLECTION_FORMATTER);
//        registerFormatter(Map.class, MAP_FORMATTER);
        registerFormatter(Date.class, DATE_FORMATTER);
        registerFormatter(UUID.class, UUID_FORMATTER);
    }

}
