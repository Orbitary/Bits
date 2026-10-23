/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrigadierArgumentsTest {

    public enum Mode { FAST, SLOW }

    public record Point(double x, double y) {}

    @SuppressWarnings("unused")
    static void signatures(Collection<? extends String> wildcard, Collection<String> plain, Collection raw) {}

    private static Type parameterType(int index) throws NoSuchMethodException {
        Method method = BrigadierArgumentsTest.class.getDeclaredMethod("signatures", Collection.class, Collection.class, Collection.class);
        return method.getGenericParameterTypes()[index];
    }

    @Test
    void primitivesShareTheirWrapperArgument() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();

        assertSame(arguments.get(Integer.class), arguments.get(int.class));
        assertSame(arguments.get(Double.class), arguments.get(double.class));
        assertSame(arguments.get(Boolean.class), arguments.get(boolean.class));
    }

    @Test
    void defaultsCoverTheBasicTypes() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();

        for (Class<?> type : List.of(String.class, Boolean.class, Integer.class, Long.class, Float.class, Double.class)) {
            assertNotNull(arguments.get(type).argumentType(), type.getName());
        }
    }

    @Test
    void unknownTypesAreRejected() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();

        assertThrows(IllegalArgumentException.class, () -> arguments.get(Point.class));
    }

    @Test
    void enumArgumentsAreCachedAndSuggestConstants() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();

        assertSame(arguments.get(Mode.class), arguments.get(Mode.class));
        assertNotNull(arguments.get(Mode.class).suggestions());
    }

    @Test
    void compositesAreFoundByRawType() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        BrigadierComposite<Point> composite = new BrigadierComposite<>(List.of(), values -> new Point(0, 0));

        arguments.registerComposite(Point.class, composite);

        assertSame(composite, arguments.composite(Point.class));
        assertNull(arguments.composite(String.class));
    }

    @Test
    void collectionsResolveByElementType() throws NoSuchMethodException {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        BrigadierArgument<Object> words = BrigadierArgument.of(StringArgumentType.word(), (context, name) -> List.of());
        arguments.registerCollection(String.class, words);

        assertSame(words, arguments.get(parameterType(0)));
        assertSame(words, arguments.get(parameterType(1)));
    }

    @Test
    void rawCollectionsAreRejected() throws NoSuchMethodException {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        arguments.registerCollection(String.class, BrigadierArgument.of(StringArgumentType.word(), (context, name) -> List.of()));

        assertThrows(IllegalArgumentException.class, () -> arguments.get(parameterType(2)));
    }

    @Test
    void collectionOfUnregisteredElementIsRejected() throws NoSuchMethodException {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();

        assertThrows(IllegalArgumentException.class, () -> arguments.get(parameterType(1)));
    }

    @Test
    void registeredArgumentsAreUsed() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        BrigadierArgument<Object> custom = BrigadierArgument.of(StringArgumentType.word(), (context, name) -> "custom");

        arguments.register(Point.class, custom);

        assertTrue(arguments.get(Point.class) == custom);
        assertEquals("custom", "custom");
    }

}
