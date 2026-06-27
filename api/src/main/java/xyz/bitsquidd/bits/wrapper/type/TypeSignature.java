/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.type;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Represents a complete type signature, including the raw class and any generic type arguments.
 * <p>
 * This is useful for capturing complex types like {@code List<Integer>} at runtime.
 * For example:
 * <ul>
 * <li>{@code TypeSignature.of(List.class, Integer.class)} represents {@code List<Integer>}
 * <li>{@code TypeSignature.of(Map.class, String.class, Float.class)} represents {@code Map<String, Float>}
 * </ul>
 *
 * @param <T> the raw type
 *
 * @since 0.0.10
 */
public final class TypeSignature<T> {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED;

    static {
        PRIMITIVE_TO_BOXED = new HashMap<>();
        PRIMITIVE_TO_BOXED.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_BOXED.put(byte.class, Byte.class);
        PRIMITIVE_TO_BOXED.put(char.class, Character.class);
        PRIMITIVE_TO_BOXED.put(short.class, Short.class);
        PRIMITIVE_TO_BOXED.put(int.class, Integer.class);
        PRIMITIVE_TO_BOXED.put(long.class, Long.class);
        PRIMITIVE_TO_BOXED.put(float.class, Float.class);
        PRIMITIVE_TO_BOXED.put(double.class, Double.class);
        PRIMITIVE_TO_BOXED.put(void.class, Void.class);
    }

    private static <X> Class<X> boxPrimitive(Class<X> clazz) {
        if (clazz.isPrimitive()) {
            @SuppressWarnings("unchecked")
            Class<X> boxed = (Class<X>)PRIMITIVE_TO_BOXED.get(clazz);
            return Objects.requireNonNull(boxed, "No boxed type found for primitive: " + clazz.getName());
        }
        return clazz;
    }

    private final Class<T> rawType;
    private final Type[] typeArguments;

    private TypeSignature(Class<T> rawType, Type @Nullable [] typeArguments) {
        this.rawType = boxPrimitive(rawType);
        this.typeArguments = typeArguments != null ? typeArguments.clone() : new Type[0];
    }

    public static TypeSignature<?> of(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            Class<?> rawType = (Class<?>)parameterizedType.getRawType();
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            return new TypeSignature<>(rawType, typeArgs);
        } else if (type instanceof Class<?> clazz) {
            return new TypeSignature<>(clazz, null);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeSignature<T> from(T obj) {
        return (TypeSignature<T>)of(obj.getClass());
    }

    public static TypeSignature<?> of(Class<?> rawType, Class<?>... typeArguments) {
        return new TypeSignature<>(rawType, typeArguments);
    }

    public Class<T> toRawType() {
        return rawType;
    }

    public T cast(Object obj) {
        return rawType.cast(obj);
    }

    public boolean matches(TypeSignature<?> other) {
        if (!rawType.equals(other.rawType)) return false;

        // Constructor always assigns typeArguments to either the cloned array or new Type[0] - it's never null post-construction.
        if (typeArguments.length != other.typeArguments.length) return false;

        for (int i = 0; i < typeArguments.length; i++) {
            Type a = typeArguments[i];
            Type b = other.typeArguments[i];
            if (a instanceof WildcardType || b instanceof WildcardType) continue; // wildcard matches anything
            if (!Objects.equals(a, b)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TypeSignature{" +
          "rawType=" + rawType +
          ", typeArguments=" + Arrays.toString(typeArguments) +
          '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TypeSignature<?> other)) return false;
        return Objects.equals(rawType, other.rawType) && Arrays.equals(typeArguments, other.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawType, Arrays.hashCode(typeArguments));
    }

}