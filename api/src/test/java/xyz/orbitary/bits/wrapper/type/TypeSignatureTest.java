/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.wrapper.type;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TypeSignatureTest {
    List<String> strings = List.of();
    List<Integer> integers = List.of();
    List<?> wildcard = List.of();
    Map<String, List<Integer>> nested = Map.of();

    private static Type type(String field) throws Exception {
        return TypeSignatureTest.class.getDeclaredField(field).getGenericType();
    }

    @Test
    void reflectedAndExplicitGenericsHaveTheSameEqualityAndHashCode() throws Exception {
        var reflected = TypeSignature.of(type("strings"));
        var explicit = TypeSignature.of(List.class, String.class);
        assertEquals(explicit, reflected);
        assertEquals(explicit.hashCode(), reflected.hashCode());
        assertNotEquals(reflected, TypeSignature.of(type("integers")));
        assertNotEquals(reflected, TypeSignature.of(List.class));
    }

    @Test
    void allPrimitiveTypesAreNormalisedToTheirWrappers() {
        var primitives = List.of(boolean.class, byte.class, char.class, short.class, int.class,
          long.class, float.class, double.class, void.class);
        var wrappers = List.of(Boolean.class, Byte.class, Character.class, Short.class, Integer.class,
          Long.class, Float.class, Double.class, Void.class);
        for (int i = 0; i < primitives.size(); i++) {
            assertEquals(TypeSignature.of(wrappers.get(i)), TypeSignature.of(primitives.get(i)));
        }
    }

    @Test
    void wildcardMatchingDoesNotChangeExactEquality() throws Exception {
        var concrete = TypeSignature.of(type("strings"));
        var wildcard = TypeSignature.of(type("wildcard"));
        assertTrue(concrete.matches(wildcard));
        assertTrue(wildcard.matches(concrete));
        assertNotEquals(concrete, wildcard);
        assertFalse(concrete.matches(TypeSignature.of(Map.class, String.class)));
        assertFalse(concrete.matches(TypeSignature.of(List.class)));
        assertFalse(concrete.matches(TypeSignature.of(type("integers"))));
    }

    @Test
    void callerCannotMutateGenericArgumentsAfterConstruction() {
        Class<?>[] arguments = {String.class};
        var signature = TypeSignature.of(List.class, arguments);
        int hash = signature.hashCode();
        arguments[0] = Integer.class;
        assertEquals(TypeSignature.of(List.class, String.class), signature);
        assertEquals(hash, signature.hashCode());
    }

    @Test
    void nestedGenericsRemainDistinct() throws Exception {
        var nested = TypeSignature.of(type("nested"));
        assertEquals(Map.class, nested.toRawType());
        assertNotEquals(TypeSignature.of(Map.class, String.class, List.class), nested);
        assertEquals(nested, TypeSignature.of(type("nested")));
    }

    @Test
    void castUsesTheRawTypeAndRejectsIncompatibleObjects() {
        var signature = TypeSignature.of(String.class);
        var value = new String("value");
        assertSame(value, signature.cast(value));
        assertThrows(ClassCastException.class, () -> signature.cast(42));
    }

    @Test
    void unsupportedReflectiveTypesAreRejected() throws Exception {
        var parameterised = (java.lang.reflect.ParameterizedType) type("wildcard");
        assertThrows(IllegalArgumentException.class,
          () -> TypeSignature.of(parameterised.getActualTypeArguments()[0]));
    }
}
