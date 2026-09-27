/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.wrapper;

import xyz.orbitary.bits.util.math.MathHelper;

import java.util.Collection;

import static xyz.orbitary.bits.util.Randomness.RANDOM;


/**
 * Represents a numerical value clamped between 0 and 1, inclusive.
 * <p>
 * This immutable class provides a type-safe way to handle percentages and
 * proportions, with static factories for common scenarios like averages or fractions.
 *
 * @since 0.0.10
 */
public final class Percentage {
    private final float value;

    private Percentage(final double value) {
        // We cast to float, as that's more than precise enough for our needs and allows for flexibility with inputs.
        this.value = (float)Math.clamp(value, 0.0, 1.0);
    }

    public static final Percentage ZERO = new Percentage(0.0f);
    public static final Percentage FULL = new Percentage(1.0f);

    public static final Percentage HALF = new Percentage(1f / 2f);
    public static final Percentage THIRD = new Percentage(1f / 3f);
    public static final Percentage QUARTER = new Percentage(1f / 4f);
    public static final Percentage FIFTH = new Percentage(1f / 5f);
    public static final Percentage SIXTH = new Percentage(1f / 6f);

    //region Constructors
    public static Percentage of(final double value) {
        return new Percentage(value);
    }

    public static Percentage random(final double min, final double max) {
        return new Percentage(RANDOM.nextFloat((float)min, (float)max));
    }

    public static Percentage ofFraction(final double numerator, final double denominator) {
        if (denominator == 0.0) return ZERO;
        return new Percentage(numerator / denominator);
    }

    public static Percentage ofAverage(Collection<Percentage> percentages) {
        if (percentages.isEmpty()) return ZERO;
        double sum = percentages.stream().mapToDouble(Percentage::get).sum();
        return new Percentage(sum / (float)percentages.size());
    }

    public static Percentage clamp(Percentage percentage, Percentage min, Percentage max) {
        return new Percentage(Math.clamp(percentage.get(), min.get(), max.get()));
    }
    //endregion


    //region Math operations
    public Percentage add(final Percentage other) {
        return new Percentage(this.value + other.value);
    }

    public Percentage subtract(final Percentage other) {
        return new Percentage(this.value - other.value);
    }


    public boolean greaterThan(final Percentage other) {
        return this.value > other.value;
    }

    public boolean lessThan(final Percentage other) {
        return this.value < other.value;
    }

    public boolean equalTo(final Percentage other) {
        return this.value == other.value;
    }

    public boolean greaterThanOrEqualTo(final Percentage other) {
        return this.value >= other.value;
    }

    public boolean lessThanOrEqualTo(final Percentage other) {
        return this.value <= other.value;
    }
    //endregion


    //region Getters
    public float get() {
        return value;
    }
    //endregion


    //region Java internals
    @Override
    public String toString() {
        return MathHelper.round(value * 100, 0) + "%";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Percentage other && this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }
    //endregion

}
