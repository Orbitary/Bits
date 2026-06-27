/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;


public final class AnnotationHelper {
    private AnnotationHelper() {}

    /**
     * Searches for an annotation in the class hierarchy and implemented interfaces, returning an Optional.
     * Checks declared annotations only, walking superclasses then interfaces.
     *
     * @since 0.0.14
     */
    public static <T extends Annotation> Optional<T> getAnnotationOptional(Class<?> clazz, Class<T> annotationClass) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            T annotation = current.getDeclaredAnnotation(annotationClass);
            if (annotation != null) return Optional.of(annotation);
            for (Class<?> iface : current.getInterfaces()) {
                Optional<T> fromInterface = getAnnotationOptional(iface, annotationClass);
                if (fromInterface.isPresent()) return fromInterface;
            }
            current = current.getSuperclass();
        }
        return Optional.empty();
    }

    /**
     * Get the value of an annotation attribute, or its default if the annotation is not present.
     *
     * @since 0.0.14
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation, T> T getValueOrDefault(Class<?> targetClass, Class<A> annotationClass, String attributeName) {
        try {
            A annotation = getAnnotationOptional(targetClass, annotationClass).orElse(null);
            Method method = annotationClass.getMethod(attributeName);
            if (annotation != null) {
                return (T)method.invoke(annotation);
            } else {
                return (T)method.getDefaultValue();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get annotation value", e);
        }
    }

}