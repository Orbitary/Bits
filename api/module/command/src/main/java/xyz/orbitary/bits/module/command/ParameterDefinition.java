/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * A command argument read from a {@link Command} method or constructor parameter.
 *
 * @param index       the position of the parameter in the signature
 * @param name        the normalised argument name
 * @param description the description, or empty for none
 * @param required    whether the argument must be supplied
 * @param type        the full generic type the argument is converted to, such as {@code Collection<Player>}
 * @param suggestions fixed values from {@link Suggest}, or an empty list for none
 *
 * @since 0.1.6
 */
public record ParameterDefinition(
  int index,
  String name,
  String description,
  boolean required,
  Type type,
  List<String> suggestions
) {

    /**
     * @return the class of {@link #type}, without its type arguments
     *
     * @since 0.1.6
     */
    public Class<?> rawType() {
        return raw(type);
    }

    /**
     * @param type a class, parameterized type or wildcard
     *
     * @return the class of the type, without its type arguments
     *
     * @throws IllegalArgumentException if the type is not a class, parameterized type or wildcard
     *
     * @since 0.1.6
     */
    public static Class<?> raw(Type type) {
        if (type instanceof Class<?> clazz) return clazz;
        if (type instanceof ParameterizedType parameterized) return raw(parameterized.getRawType());
        if (type instanceof WildcardType wildcard) return raw(wildcard.getUpperBounds()[0]);
        throw new IllegalArgumentException("Unsupported parameter type: " + type);
    }

}
