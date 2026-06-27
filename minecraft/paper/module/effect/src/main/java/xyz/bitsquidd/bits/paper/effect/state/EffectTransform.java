/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.state;

@FunctionalInterface
public interface EffectTransform {
    EffectModifier transform(EffectModifier data);

    static EffectTransform identity() {
        return data -> data;
    }


    default EffectTransform andThen(EffectTransform after) {
        return data -> after.transform(this.transform(data));
    }


    final class Amplifier {
        private Amplifier() {}

        public static EffectTransform constant(int amplifier) {
            return data -> new EffectModifier(amplifier, data.durationTicks());
        }

        public static EffectTransform proportional(double multiplier) {
            return data -> new EffectModifier((int)(data.amplifier() * multiplier), data.durationTicks());
        }

    }


    final class Duration {
        private Duration() {}

        public static EffectTransform constant(int durationTicks) {
            return data -> new EffectModifier(data.amplifier(), durationTicks);
        }

        public static EffectTransform proportional(double multiplier) {
            return data -> new EffectModifier(data.amplifier(), (int)(data.durationTicks() * multiplier));
        }

        public static EffectTransform instant() {
            return data -> new EffectModifier(data.amplifier(), 1);
        }

        public static EffectTransform permanent() {
            return data -> new EffectModifier(data.amplifier(), -1);
        }

    }

}
