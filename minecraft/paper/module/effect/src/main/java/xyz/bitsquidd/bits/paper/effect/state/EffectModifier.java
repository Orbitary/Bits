/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.state;


/**
 * Defines the strength and duration of an effect.
 * <p>
 * The amplifier is 1-indexed: a value of {@code 1} represents the base strength.
 * Duration is measured in server ticks; a negative value represents an infinite effect.
 *
 * @param amplifier     the effect strength, 1-indexed
 * @param durationTicks the effect duration in server ticks, or negative for infinite
 *
 * @since 0.0.21
 */
public record EffectModifier(
  int amplifier,
  int durationTicks
) {
    /**
     * The default modifier: amplifier 1 for 200 ticks (10 seconds).
     */
    public static final EffectModifier DEFAULT = new EffectModifier(1, 200);

    /**
     * An infinite-duration modifier at amplifier 1.
     */
    public static final EffectModifier PERMANENT = new EffectModifier(1, -1);

    /**
     * Creates a new modifier with the given amplifier and duration.
     *
     * @param amplifier     the effect strength, 1-indexed
     * @param durationTicks the effect duration in server ticks, or negative for infinite
     *
     * @return a new modifier
     *
     * @since 0.0.21
     */
    public static EffectModifier of(int amplifier, int durationTicks) {
        return new EffectModifier(amplifier, durationTicks);
    }


    /**
     * Creates a new modifier with the given amplifier and the default duration.
     *
     * @param amplifier the effect strength, 1-indexed
     *
     * @since 0.0.21
     */
    public static EffectModifier ofAmplifier(int amplifier) {
        return new EffectModifier(amplifier, DEFAULT.durationTicks);
    }

    /**
     * Creates a default modifier with a given amplifier and the default duration.
     *
     * @param durationTicks the effect duration in server ticks, or negative for infinite
     *
     * @since 0.0.21
     */
    public static EffectModifier ofDuration(int durationTicks) {
        return new EffectModifier(DEFAULT.amplifier, durationTicks);
    }


}
