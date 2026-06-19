/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.wrapper;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Provides static utility methods for searching and resolving {@link Enum} constants.
 *
 * @since 0.0.10
 */
public final class Enums {
    private Enums() {}

    /**
     * Resolves an enum constant by its name, ignoring case sensitivity.
     *
     * @param <E>       the enum type
     * @param enumClass the class of the enum
     * @param name      the name of the constant to find, may be null
     *
     * @return the resolved constant, or null if not found or name is null
     *
     * @since 0.0.10
     */
    public static <E extends Enum<E>> Optional<E> valueOf(Class<E> enumClass, @Nullable String name) {
        return Optional.ofNullable(valueOfOrDefault(enumClass, name, null));
    }

    /**
     * Resolves an enum constant by its name (case-insensitive), or returns a default value if not found.
     *
     * @param <E>          the enum type
     * @param enumClass    the class of the enum
     * @param name         the name of the constant to find, may be null
     * @param defaultValue the value to return if resolution fails
     *
     * @return the resolved constant or the default value
     *
     * @since 0.0.10
     */
    public static <E extends Enum<E>> @Nullable E valueOfOrDefault(Class<E> enumClass, @Nullable String name, @Nullable E defaultValue) {
        if (name == null) return defaultValue;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Converts an enum value to a stable string identifier, the lowercase name of the constant.
     *
     * @since 0.0.15
     */
    public static <E extends Enum<E>> String toIdentifier(E constant) {
        return constant.name().toLowerCase(Locale.ROOT);
    }


    /**
     * Resolves an enum constant by a string identifier derived from the constant, ignoring case sensitivity.
     *
     * @since 0.0.20
     */
    public static <E extends Enum<E>> Optional<E> getFromIdentifier(Class<E> enumClass, String identifier) {
        return getFromIdentifier(enumClass, constant -> toIdentifier(constant).equals(identifier));
    }

    /**
     * Searches for an enum constant that matches a specific predicate.
     *
     * @param <E>        the enum type
     * @param enumClass  the class of the enum
     * @param identifier the condition to match against each constant
     *
     * @return an optional containing the first matching constant
     *
     * @since 0.0.10
     */
    public static <E extends Enum<E>> Optional<E> getFromIdentifier(Class<E> enumClass, Predicate<E> identifier) {
        for (E constant : enumClass.getEnumConstants()) {
            if (identifier.test(constant)) return Optional.of(constant);
        }
        return Optional.empty();
    }

    public static <E extends Enum<E>> Comparator<E> comparingId() {
        return Comparator.comparing(Enums::toIdentifier);
    }

    /**
     * Returns the set of values obtained by applying a mapping function to each constant of the enum.
     *
     * @param <E>         the enum type
     * @param <V>         the type of values to return
     * @param enumClass   the class of the enum
     * @param valueMapper a function that maps each enum constant to a value
     *
     * @return a collection of values obtained from the enum constants
     *
     * @since 0.0.13
     */
    public static <E extends Enum<E>, V> Collection<V> getValuesFromEnum(Class<E> enumClass, Function<E, V> valueMapper) {
        return Arrays.stream(enumClass.getEnumConstants())
          .map(valueMapper)
          .toList();
    }

    /**
     * Overload, returning the set of names of the constants of the enum, ignoring case sensitivity.
     *
     * @since 0.0.13
     */
    public static <E extends Enum<E>> Collection<String> getValuesFromEnum(Class<E> enumClass) {
        return getValuesFromEnum(enumClass, Enum::name);
    }

    /**
     * Get the next enum constant in the order they are declared, wrapping around to the first constant after reaching the end.
     *
     * @param <E>     the enum type
     * @param current the current enum constant
     *
     * @return the next enum constant, or null if the current constant is not found in the enum
     *
     * @since 0.0.15
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> Optional<E> getNextConstant(E current) {
        E[] constants = ((Class<E>)current.getClass()).getEnumConstants();
        for (int i = 0; i < constants.length; i++) {
            if (constants[i] == current) {
                return Optional.of(constants[(i + 1) % constants.length]);
            }
        }
        return Optional.empty();
    }


    /**
     * Returns the set of enum constants that match a specific predicate.
     *
     * @since 0.0.14
     */
    public static <E extends Enum<E>> Collection<E> filterConstants(Class<E> enumClass, Predicate<E> filter) {
        return Arrays.stream(enumClass.getEnumConstants())
          .filter(filter)
          .toList();
    }

}
