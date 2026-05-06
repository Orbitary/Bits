/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.serializer.jackson;


import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Custom Jackson annotation introspector that treats fields annotated with @Nullable as optional (not required).
 * It forces the serializer to treat everything as required unless @Nullable.
 */
public class NullableAwareIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public Boolean hasRequiredMarker(AnnotatedMember m) {
        return !isNullable(m);
    }

    private boolean isNullable(Annotated a) {
        return a.hasAnnotation(org.jetbrains.annotations.Nullable.class)
          || a.hasAnnotation(org.jspecify.annotations.Nullable.class);
    }

}