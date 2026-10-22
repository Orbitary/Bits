/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.wrapper;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PercentageTest {

    @Test
    void valuesOutsideZeroToOneAreClamped() {
        assertEquals(Percentage.FULL, Percentage.of(1.5));
        assertEquals(Percentage.ZERO, Percentage.of(-0.2));
    }

    @Test
    void fractionClampsAndTreatsZeroDenominatorAsZero() {
        assertEquals(Percentage.QUARTER, Percentage.ofFraction(1, 4));
        assertEquals(Percentage.FULL, Percentage.ofFraction(3, 2));
        assertEquals(Percentage.ZERO, Percentage.ofFraction(1, 0));
    }

    @Test
    void averageOfNothingIsZeroAndOtherwiseTheMean() {
        assertEquals(Percentage.ZERO, Percentage.ofAverage(List.of()));
        assertEquals(Percentage.HALF, Percentage.ofAverage(List.of(Percentage.ZERO, Percentage.FULL)));
    }

    @Test
    void arithmeticClampsTheResult() {
        assertEquals(Percentage.FULL, Percentage.HALF.add(Percentage.HALF));
        assertEquals(Percentage.FULL, Percentage.FULL.add(Percentage.HALF));
        assertEquals(Percentage.ZERO, Percentage.HALF.subtract(Percentage.FULL));
    }

    @Test
    void clampKeepsValueWithinBounds() {
        assertEquals(Percentage.HALF, Percentage.clamp(Percentage.FULL, Percentage.ZERO, Percentage.HALF));
        assertEquals(Percentage.HALF, Percentage.clamp(Percentage.ZERO, Percentage.HALF, Percentage.FULL));
    }

    @Test
    void comparisonsFollowTheValue() {
        assertTrue(Percentage.THIRD.lessThan(Percentage.HALF));
        assertTrue(Percentage.HALF.greaterThan(Percentage.THIRD));
        assertTrue(Percentage.HALF.equalTo(Percentage.of(0.5)));
        assertTrue(Percentage.HALF.lessThanOrEqualTo(Percentage.HALF));
        assertTrue(Percentage.HALF.greaterThanOrEqualTo(Percentage.HALF));
        assertFalse(Percentage.HALF.lessThan(Percentage.HALF));
    }

    @Test
    void equalValuesAreEqualWithTheSameHash() {
        assertEquals(Percentage.of(0.5), Percentage.HALF);
        assertEquals(Percentage.of(0.5).hashCode(), Percentage.HALF.hashCode());
    }

}
