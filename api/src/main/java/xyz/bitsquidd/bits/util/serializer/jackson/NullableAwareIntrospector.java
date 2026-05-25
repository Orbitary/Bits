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
import org.jetbrains.annotations.Nullable;


/**
 * Custom Jackson annotation introspector that treats fields annotated with @Nullable as explicitly optional.
 * Unannotated fields defer to Jackson's default behaviour (respects FAIL_ON_MISSING_CREATOR_PROPERTIES).
 */
public class NullableAwareIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public @Nullable Boolean hasRequiredMarker(AnnotatedMember m) {
        if (isNullable(m)) return false;
        return null;
    }

    private boolean isNullable(Annotated a) {
        return a.hasAnnotation(org.jetbrains.annotations.Nullable.class)
          || a.hasAnnotation(org.jspecify.annotations.Nullable.class);
    }

}