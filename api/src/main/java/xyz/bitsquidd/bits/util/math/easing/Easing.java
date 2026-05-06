/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.math.easing;

import org.jetbrains.annotations.Range;

import java.util.function.Function;

/**
 * Represents an easing function that maps a progress value (between 0 and 1) to an eased progress value.
 *
 * @since 0.0.13
 */
public abstract class Easing {
    private static final double EASING_MAGIC = 1.70158;

    private static final double PI = Math.PI;
    private static final double TAU = 2 * PI;
    private static final double HALF_PI = PI / 2;


    public abstract float progress(@Range(from = 0, to = 1) float t);


    //region Composite methods

    /**
     * Reverses this easing: reversed.progress(t) == this.progress(1 - t)
     */
    public Easing reversed() {
        Easing self = this;
        return new Easing() {
            @Override
            public float progress(@Range(from = 0, to = 1) float t) {
                return self.progress(1f - t);
            }
        };
    }

    /**
     * Loops this easing: looped.progress(t) == this.progress(2 * t) for t <= 0.5, and this.progress(2 - 2 * t) for t > 0.5
     */
    public Easing looped() {
        Easing self = this;
        return new Easing() {
            @Override
            public float progress(@Range(from = 0, to = 1) float t) {
                return self.progress(t * 2 <= 1 ? t * 2 : (2 - t * 2));
            }
        };
    }

    /**
     * Blends this easing with another by a factor (0 = this, 1 = other)
     */
    public Easing blend(Easing other, float factor) {
        Easing self = this;
        return new Easing() {
            @Override
            public float progress(@Range(from = 0, to = 1) float t) {
                return self.progress(t) * (1f - factor) + other.progress(t) * factor;
            }
        };
    }

    /**
     * Returns an easing that maps t through this, then through the other
     */
    public Easing then(Easing other) {
        Easing self = this;
        return new Easing() {
            @Override
            public float progress(@Range(from = 0, to = 1) float t) {
                return other.progress(self.progress(t));
            }
        };
    }

    //endregion

    //region Static Factory
    public static Easing of(Function<? super Float, Float> fn) {
        return new Easing() {
            @Override
            public float progress(@Range(from = 0, to = 1) float t) {
                return fn.apply(t);
            }
        };
    }
    //endregion


    //region Implementations
    //region Linear

    public static final class Linear extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return t;
        }

    }

    //endregion


    //region Curved

    /**
     * Ease-in with a configurable exponent. Default (no-arg) is quadratic (exponent = 2).
     */
    public static final class In extends Easing {
        private final float exponent;

        public In() {
            this(2f);
        }

        public In(float exponent) {
            this.exponent = exponent;
        }

        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)Math.pow(t, exponent);
        }

    }

    /**
     * Ease-out with a configurable exponent. Default (no-arg) is quadratic (exponent = 2).
     */
    public static final class Out extends Easing {
        private final float exponent;

        public Out() {
            this(2f);
        }

        public Out(float exponent) {
            this.exponent = exponent;
        }

        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)(1 - Math.pow(1 - t, exponent));
        }

    }

    /**
     * Ease-in-out with a configurable exponent. Default (no-arg) is quadratic (exponent = 2).
     */
    public static final class InOut extends Easing {
        private final float exponent;

        public InOut() {
            this(2f);
        }

        public InOut(float exponent) {
            this.exponent = exponent;
        }

        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            if (t < 0.5f) return (float)(Math.pow(t * 2, exponent) / 2f);
            return (float)(1 - Math.pow((1 - t) * 2, exponent) / 2f);
        }

    }

    //endregion


    //region Trigonometric

    public static final class InSin extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)(1 - Math.cos(t * HALF_PI));
        }

    }

    public static final class OutSin extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)(Math.sin(t * HALF_PI));
        }

    }

    public static final class InOutSin extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)(-(Math.cos(PI * t) - 1) / 2f);
        }

    }

    //endregion


    //region Exponential

    public static final class InExpo extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return t == 0 ? 0f : (float)Math.pow(2, 10 * (t - 1));
        }

    }

    public static final class OutExpo extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return t == 1 ? 1f : (float)(1 - Math.pow(2, -10 * t));
        }

    }

    public static final class InOutExpo extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            if (t <= 0) return 0f;
            if (t >= 1) return 1f;
            if (t < 0.5f) return (float)(Math.pow(2, 20 * t - 10) / 2);
            return (float)((2 - Math.pow(2, -20 * t + 10)) / 2f);
        }

    }

    //endregion


    //region Overshoot

    public static final class InBack extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)((EASING_MAGIC + 1) * t * t * t - EASING_MAGIC * t * t);
        }

    }

    public static final class OutBack extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)(1 + (EASING_MAGIC + 1) * Math.pow(t - 1, 3) + EASING_MAGIC * Math.pow(t - 1, 2));
        }

    }

    public static final class InOutBack extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            double c = EASING_MAGIC * 1.525;
            if (t <= 0) return 0f;
            if (t >= 1) return 1f;
            if (t < 0.5f) return (float)(Math.pow(2 * t, 2) * ((c + 1) * 2 * t - c)) / 2;
            return (float)(Math.pow(2 * t - 2, 2) * ((c + 1) * (t * 2 - 2) + c) + 2) / 2;
        }

    }

    //endregion


    //region Circular

    public static final class InCirc extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return 1 - (float)Math.sqrt(1 - t * t);
        }

    }

    public static final class OutCirc extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return (float)Math.sqrt(1 - Math.pow(t - 1, 2));
        }

    }

    public static final class InOutCirc extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            if (t < 0.5f) return (1 - (float)Math.sqrt(1 - Math.pow(2 * t, 2))) / 2;
            return ((float)Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2;
        }

    }

    //endregion


    //region Elastic

    public static final class InElastic extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            if (t == 0) return 0f;
            if (t == 1) return 1f;
            double c = TAU / 3;
            return (float)(-Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c));
        }

    }

    public static final class OutElastic extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            if (t == 0) return 0f;
            if (t == 1) return 1f;
            double c = TAU / 3;
            return (float)(Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c) + 1);
        }

    }

    public static final class InOutElastic extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            if (t == 0) return 0f;
            if (t == 1) return 1f;
            double c = TAU / 4.5;
            if (t < 0.5f) return (float)(-(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * c)) / 2);
            return (float)((Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * c)) / 2 + 1);
        }

    }

    //endregion


    //region Bounce

    public static final class OutBounce extends Easing {
        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            double c1 = 7.5625, c2 = 2.75;
            if (t < 1 / c2) return (float)(c1 * t * t);
            if (t < 2 / c2) return (float)(c1 * (t -= (float)(1.5 / c2)) * t + 0.75);
            if (t < 2.5 / c2) return (float)(c1 * (t -= (float)(2.25 / c2)) * t + 0.9375);
            return (float)(c1 * (t -= (float)(2.625 / c2)) * t + 0.984375);
        }

    }

    public static final class InBounce extends Easing {
        private static final OutBounce OUT = new OutBounce();

        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            return 1 - OUT.progress(1 - t);
        }

    }

    public static final class InOutBounce extends Easing {
        private static final InBounce IN = new InBounce();
        private static final OutBounce OUT = new OutBounce();

        @Override
        public float progress(@Range(from = 0, to = 1) float t) {
            if (t < 0.5f) return IN.progress(t * 2) / 2;
            return (OUT.progress(t * 2 - 1) + 1) / 2;
        }

    }

    //endregion
    //endregion


}
