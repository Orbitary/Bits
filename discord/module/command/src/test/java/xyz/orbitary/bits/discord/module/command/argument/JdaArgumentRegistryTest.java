/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command.argument;

import org.junit.jupiter.api.Test;
import xyz.orbitary.bits.discord.module.command.argument.impl.GenericEnumParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.IntegerParser;
import xyz.orbitary.bits.discord.module.command.argument.impl.StringParser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JdaArgumentRegistryTest {

    private final JdaArgumentRegistry registry = JdaArgumentRegistry.withDefaults();

    enum Mode { FAST, SLOW }

    @Test
    void primitivesUseTheirWrapperParser() {
        assertInstanceOf(IntegerParser.class, registry.parser(int.class));
        assertSame(registry.parser(Integer.class), registry.parser(int.class));
    }

    @Test
    void registeredParserIsReturned() {
        assertInstanceOf(StringParser.class, registry.parser(String.class));
    }

    @Test
    void enumsWithoutAParserGetTheGenericOne() {
        assertInstanceOf(GenericEnumParser.class, registry.parser(Mode.class));
    }

    @Test
    void unknownTypesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> registry.parser(Object.class));
    }

}
