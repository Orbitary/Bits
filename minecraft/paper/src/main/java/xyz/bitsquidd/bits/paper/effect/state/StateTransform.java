/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.state;

@FunctionalInterface
public interface StateTransform {
    EffectModifier transform(EffectModifier data);

    static StateTransform identity() {
        return data -> data;
    }


    default StateTransform andThen(StateTransform after) {
        return data -> after.transform(this.transform(data));
    }


    final class Amplifier {
        private Amplifier() {}

        public static StateTransform constant(int amplifier) {
            return data -> new EffectModifier(amplifier, data.durationTicks());
        }

        public static StateTransform proportional(double multiplier) {
            return data -> new EffectModifier((int)(data.amplifier() * multiplier), data.durationTicks());
        }

    }


    final class Duration {
        private Duration() {}

        public static StateTransform constant(int durationTicks) {
            return data -> new EffectModifier(data.amplifier(), durationTicks);
        }

        public static StateTransform proportional(double multiplier) {
            return data -> new EffectModifier(data.amplifier(), (int)(data.durationTicks() * multiplier));
        }

        public static StateTransform instant() {
            return data -> new EffectModifier(data.amplifier(), 1);
        }

        public static StateTransform permanent() {
            return data -> new EffectModifier(data.amplifier(), -1);
        }

    }

}
