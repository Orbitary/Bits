/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.state;

/**
 * A function that maps one {@link EffectModifier} to another, used to override or scale
 * the amplifier or duration of an effect when it is applied or composed into a parent effect.
 *
 * @since 0.0.21
 */
@FunctionalInterface
public interface EffectTransform {

    /**
     * Applies this transform to the given modifier.
     *
     * @param data the modifier to transform
     * @return the transformed modifier
     *
     * @since 0.0.21
     */
    EffectModifier transform(EffectModifier data);

    /**
     * Returns a transform that passes the modifier through unchanged.
     *
     * @return the identity transform
     *
     * @since 0.0.21
     */
    static EffectTransform identity() {
        return data -> data;
    }

    /**
     * Returns a composed transform that applies this transform first, then {@code after}.
     *
     * @param after the transform to apply after this one
     * @return the composed transform
     *
     * @since 0.0.21
     */
    default EffectTransform andThen(EffectTransform after) {
        return data -> after.transform(this.transform(data));
    }


    /**
     * Pre-built transforms that affect only the amplifier, leaving duration unchanged.
     *
     * @since 0.0.21
     */
    final class Amplifier {
        private Amplifier() {}

        /**
         * Returns a transform that sets the amplifier to a fixed value.
         *
         * @param amplifier the amplifier to set
         * @return the transform
         *
         * @since 0.0.21
         */
        public static EffectTransform constant(int amplifier) {
            return data -> new EffectModifier(amplifier, data.durationTicks());
        }

        /**
         * Returns a transform that multiplies the amplifier by a scalar.
         *
         * @param multiplier the scalar to apply
         * @return the transform
         *
         * @since 0.0.21
         */
        public static EffectTransform proportional(double multiplier) {
            return data -> new EffectModifier((int)(data.amplifier() * multiplier), data.durationTicks());
        }

    }


    /**
     * Pre-built transforms that affect only the duration, leaving amplifier unchanged.
     *
     * @since 0.0.21
     */
    final class Duration {
        private Duration() {}

        /**
         * Returns a transform that sets the duration to a fixed number of ticks.
         *
         * @param durationTicks the duration to set in ticks
         * @return the transform
         *
         * @since 0.0.21
         */
        public static EffectTransform constant(int durationTicks) {
            return data -> new EffectModifier(data.amplifier(), durationTicks);
        }

        /**
         * Returns a transform that multiplies the duration by a scalar.
         *
         * @param multiplier the scalar to apply
         * @return the transform
         *
         * @since 0.0.21
         */
        public static EffectTransform proportional(double multiplier) {
            return data -> new EffectModifier(data.amplifier(), (int)(data.durationTicks() * multiplier));
        }

        /**
         * Returns a transform that sets the duration to a single tick.
         *
         * @return the transform
         *
         * @since 0.0.21
         */
        public static EffectTransform instant() {
            return data -> new EffectModifier(data.amplifier(), 1);
        }

        /**
         * Returns a transform that sets the duration to infinite (negative value).
         *
         * @return the transform
         *
         * @since 0.0.21
         */
        public static EffectTransform permanent() {
            return data -> new EffectModifier(data.amplifier(), -1);
        }

    }

}
