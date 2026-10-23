/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterDefinitionTest {

    @SuppressWarnings("unused")
    static void signatures(Collection<String> plain, Collection<? extends Number> wildcard, int primitive) {}

    private static ParameterDefinition define(int index) throws NoSuchMethodException {
        Method method = ParameterDefinitionTest.class.getDeclaredMethod("signatures", Collection.class, Collection.class, int.class);
        return new ParameterDefinition(index, "name", "", true, method.getGenericParameterTypes()[index], List.of());
    }

    @Test
    void rawTypeOfAPlainClassIsTheClass() throws NoSuchMethodException {
        assertEquals(int.class, define(2).rawType());
    }

    @Test
    void rawTypeOfAParameterizedTypeIsTheRawClass() throws NoSuchMethodException {
        assertEquals(Collection.class, define(0).rawType());
    }

    @Test
    void rawTypeOfAWildcardElementIsTheRawClassOfItsUpperBound() throws NoSuchMethodException {
        assertEquals(Collection.class, define(1).rawType());
    }

    @Test
    void typeKeepsTheGenericArguments() throws NoSuchMethodException {
        assertEquals("java.util.Collection<java.lang.String>", define(0).type().getTypeName());
    }

    @Test
    void unsupportedTypesAreRejected() {
        ParameterDefinition definition = new ParameterDefinition(0, "name", "", true, List.class.getTypeParameters()[0], List.of());

        assertThrows(IllegalArgumentException.class, definition::rawType);
    }

}
