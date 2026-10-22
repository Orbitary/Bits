/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.wrapper.collection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderedSetTest {

    private static OrderedSet<String> setOf(String... elements) {
        final var set = new OrderedSet<String>();
        for (final String element : elements) set.add(element);
        return set;
    }

    @Test
    void iterationFollowsInsertionOrder() {
        final var set = setOf("b", "a", "c");
        assertEquals("b", set.get(0));
        assertEquals(List.of("b", "a", "c"), set.stream().toList());
    }

    @Test
    void duplicateAddIsRejectedAndKeepsTheOriginalPosition() {
        final var set = setOf("a", "b");
        assertFalse(set.add("a"));
        assertEquals(2, set.size());
        assertEquals("a", set.get(0));
    }

    @Test
    void removeKeepsTheRemainingOrder() {
        final var set = setOf("a", "b", "c");
        assertTrue(set.remove("b"));
        assertFalse(set.remove("missing"));
        assertEquals(List.of("a", "c"), set.stream().toList());
        assertFalse(set.contains("b"));
    }

    @Test
    void clearEmptiesTheSet() {
        final var set = setOf("a");
        set.clear();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
    }

    @Test
    void equalityIgnoresOrder() {
        assertEquals(setOf("a", "b"), setOf("b", "a"));
        assertEquals(setOf("a", "b").hashCode(), setOf("b", "a").hashCode());
    }

}
