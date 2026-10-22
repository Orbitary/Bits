/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.format;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatterTest {
    static final class Sample {
        @Override
        public String toString() {
            return "plain";
        }
    }

    @Test
    void nullFormatsAsNullText() {
        assertEquals("null", Formatter.format(null));
    }

    @Test
    void unregisteredObjectUsesToString() {
        assertEquals("plain", Formatter.format(new Sample()));
    }

    @Test
    void uuidUsesItsStandardForm() {
        final var uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        assertEquals(uuid.toString(), Formatter.format(uuid));
    }

    @Test
    void registeredFormatterAppliesToTheClass() {
        Formatter.registerFormatter(Sample.class, sample -> "custom");
        assertEquals("custom", Formatter.format(new Sample()));
    }

}
