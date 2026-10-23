/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RandomnessTest {
    @Test
    void seededSelectionPreservesIdentityAndGeneratorStateAcrossCollectionTypes() {
        Object[] values = {new Object(), new Object(), new Object(), new Object()};
        for (var collection : List.of(Arrays.asList(values), new ArrayList<>(Arrays.asList(values)),
          new LinkedList<>(Arrays.asList(values)), new LinkedHashSet<>(Arrays.asList(values)))) {
            for (int seed = 0; seed < 100; seed++) {
                var expected = new SplittableRandom(seed);
                var actual = new SplittableRandom(seed);
                assertSame(values[expected.nextInt(values.length)], Randomness.getRandomElement(collection, actual));
                assertEquals(expected.nextLong(), actual.nextLong());
                assertArrayEquals(values, collection.toArray());
            }
        }
    }

    @Test
    void emptyCollectionDoesNotConsumeRandomness() {
        var actual = new SplittableRandom(7);
        var error = assertThrows(IllegalArgumentException.class, () -> Randomness.getRandomElement(List.of(), actual));
        assertEquals("Collection is empty, cannot get random element.", error.getMessage());
        assertEquals(new SplittableRandom(7).nextLong(), actual.nextLong());
    }

    @Test
    void selectedNullStillThrowsAndUnselectedNullIsAllowed() {
        for (var collection : List.of(Arrays.asList(null, "value"), new LinkedList<>(Arrays.asList(null, "value")))) {
            for (int seed = 0; seed < 20; seed++) {
                int index = new SplittableRandom(seed).nextInt(2);
                var actual = new SplittableRandom(seed);
                if (index == 0) assertThrows(NullPointerException.class, () -> Randomness.getRandomElement(collection, actual));
                else assertEquals("value", Randomness.getRandomElement(collection, actual));
            }
        }
    }

    @Test
    void convenienceOverloadsRetainTheirContracts() {
        Object value = new Object();
        assertSame(value, Randomness.getRandomElement(new Object[]{value}));
        assertSame(value, Randomness.getRandomElement(List.of(value), 4));
        assertSame(value, Randomness.getRandomElement(Stream.of(value)).orElseThrow());
        assertTrue(Randomness.getRandomElement(Stream.empty()).isEmpty());
    }
}
