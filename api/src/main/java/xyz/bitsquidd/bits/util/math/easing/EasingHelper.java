/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.math.easing;

import org.jetbrains.annotations.Range;

/**
 * Provides static methods for various easing functions used in smooth animations and transitions.
 * <p>
 * Each function accepts a time parameter {@code t} representing the progress of an animation,
 * where {@code 0.0} is the start and {@code 1.0} is the end.
 *
 * @since 0.0.10
 */
@Deprecated(forRemoval = true, since = "0.0.13")
public final class EasingHelper {
    private EasingHelper() {}

    private static final double EASING_MAGIC = 1.70158;

    private static final double PI = Math.PI;
    private static final double TAU = 2 * PI;
    private static final double HALF_PI = PI / 2;


    //region Linear

    /**
     * Applies linear interpolation (no easing).
     *
     * @since 0.0.10
     */
    public static float linear(@Range(from = 0, to = 1) float t) {
        return t;
    }

    /**
     * Applies a linear interpolation back to the start value, creating a looping effect.
     *
     * @since 0.0.10
     */
    public static float backLinear(@Range(from = 0, to = 1) float t) {
        return t * 2 <= 1 ? t * 2 : (2 - t * 2);
    }

    //endregion


    //region Curved

    /**
     * Applies an ease-in transformation with a custom exponent.
     *
     * @param t     the progress, between 0.0 and 1.0
     * @param index the power to apply (e.g., 2.0 for quadratic)
     *
     * @since 0.0.10
     */
    public static float in(@Range(from = 0, to = 1) float t, float index) {
        return (float)(Math.pow(t, index));
    }

    /**
     * Applies a default quadratic ease-in transformation.
     *
     * @since 0.0.10
     */
    public static float in(@Range(from = 0, to = 1) float t) {
        return in(t, 2);
    }

    /**
     * Applies an ease-out transformation with a custom exponent.
     *
     * @param t     the progress, between 0.0 and 1.0
     * @param index the power to apply
     *
     * @since 0.0.10
     */
    public static float out(@Range(from = 0, to = 1) float t, float index) {
        return (float)(1 - Math.pow(1 - t, index));
    }

    /**
     * Applies a default quadratic ease-out transformation.
     *
     * @since 0.0.10
     */
    public static float out(@Range(from = 0, to = 1) float t) {
        return out(t, 2);
    }

    /**
     * Applies an ease-in-out transformation with a custom exponent.
     *
     * @param t     the progress, between 0.0 and 1.0
     * @param index the power to apply
     *
     * @since 0.0.10
     */
    public static float inOut(@Range(from = 0, to = 1) float t, float index) {
        if (t < 0.5) {
            return in(t * 2, index) / 2f;
        } else {
            return out((1 - t) * 2, index) / 2f + 0.5f;
        }
    }

    /**
     * Blends a linear transition with an ease-in-out transition.
     *
     * @param t          the progress, between 0.0 and 1.0
     * @param index      the power for the easing component
     * @param multiplier the blending ratio (0.0 for linear, 1.0 for ease-in-out)
     *
     * @since 0.0.10
     */
    public static float inOutSmooth(float t, float index, @Range(from = 0, to = 1) float multiplier) {
        float eased = inOut(t, index);
        return (1 - multiplier) * t + multiplier * eased;
    }
    //endregion


    //region Trigonometric

    /**
     * Applies a sine-based ease-in transformation.
     *
     * @since 0.0.10
     */
    public static float inSin(@Range(from = 0, to = 1) float t) {
        return (float)(1 - Math.cos((t * PI) / 2f));
    }

    /**
     * Applies a sine-based ease-out transformation.
     *
     * @since 0.0.10
     */
    public static float outSin(@Range(from = 0, to = 1) float t) {
        return (float)(Math.sin((t * PI) / 2f));
    }

    /**
     * Applies a sine-based ease-in-out transformation.
     *
     * @since 0.0.10
     */
    public static float inOutSin(@Range(from = 0, to = 1) float t) {
        return (float)(-(Math.cos(PI * t) - 1) / 2f);
    }

    /**
     * Applies a sine-based transformation that returns to the start value at the end, creating a looping effect.
     *
     * @since 0.0.10
     */
    public static float backSin(@Range(from = 0, to = 1) float t) {
        return (float)(Math.sin(t * TAU));
    }

    //endregion


    //region Exponential

    /**
     * Applies an exponential ease-in transformation.
     *
     * @since 0.0.10
     */
    public static float inExpo(@Range(from = 0, to = 1) float t) {
        return (float)(t == 0 ? 0f : Math.pow(2, 10 * (t - 1)));
    }

    /**
     * Applies an exponential ease-out transformation.
     *
     * @since 0.0.10
     */
    public static float outExpo(@Range(from = 0, to = 1) float t) {
        return (float)(t == 1 ? 1f : 1 - Math.pow(2, -10 * t));
    }

    /**
     * Applies an exponential ease-in-out transformation.
     *
     * @since 0.0.10
     */
    public static float inOutExpo(@Range(from = 0, to = 1) float t) {
        if (t <= 0) return 0;
        if (t >= 1) return 1;
        if (t < 0.5) return (float)(Math.pow(2, 20 * t - 10) / 2);
        return (float)((2 - Math.pow(2, -20 * t + 10)) / 2f);
    }
    //endregion


    //region Overshooting

    /**
     * Applies an ease-in-back transformation with an overshooting effect.
     *
     * @since 0.0.10
     */
    public static float inBack(@Range(from = 0, to = 1) float t) {
        return (float)((EASING_MAGIC + 1) * t * t * t - EASING_MAGIC * t * t);
    }

    /**
     * Applies an ease-out-back transformation with an overshooting effect.
     *
     * @since 0.0.10
     */
    public static float outBack(@Range(from = 0, to = 1) float t) {
        return (float)(1 + (EASING_MAGIC + 1) * Math.pow(t - 1, 3) + EASING_MAGIC * Math.pow(t - 1, 2));
    }

    /**
     * Applies an ease-in-out-back transformation with overshooting at both ends.
     *
     * @since 0.0.10
     */
    public static float inOutBack(@Range(from = 0, to = 1) float t) {
        double c = EASING_MAGIC * 1.525;

        if (t <= 0) return 0;
        if (t >= 1) return 1;
        if (t < 0.5) return (float)(Math.pow(2 * t, 2) * ((c + 1) * 2 * t - c)) / 2;
        return (float)(Math.pow(2 * t - 2, 2) * ((c + 1) * (t * 2 - 2) + c) + 2) / 2;
    }
    //endregion


    //region Circular

    /**
     * Applies a circular ease-in transformation.
     *
     * @since 0.0.10
     */
    public static float inCirc(@Range(from = 0, to = 1) float t) {
        return 1 - (float)Math.sqrt(1 - t * t);
    }

    /**
     * Applies a circular ease-out transformation.
     *
     * @since 0.0.10
     */
    public static float outCirc(@Range(from = 0, to = 1) float t) {
        return (float)Math.sqrt(1 - Math.pow(t - 1, 2));
    }

    /**
     * Applies a circular ease-in-out transformation.
     *
     * @since 0.0.10
     */
    public static float inOutCirc(@Range(from = 0, to = 1) float t) {
        if (t < 0.5) return (1 - (float)Math.sqrt(1 - Math.pow(2 * t, 2))) / 2;
        return ((float)Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2;
    }
    //endregion


    //region Elastic

    /**
     * Applies an elastic ease-in transformation with a spring effect.
     *
     * @since 0.0.10
     */
    public static float inElastic(@Range(from = 0, to = 1) float t) {
        double c = TAU / 3;

        if (t == 0) return 0;
        if (t == 1) return 1;
        return (float)(-Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c));
    }

    /**
     * Applies an elastic ease-out transformation with a spring effect.
     *
     * @since 0.0.10
     */
    public static float outElastic(@Range(from = 0, to = 1) float t) {
        double c = TAU / 3;

        if (t == 0) return 0;
        if (t == 1) return 1;
        return (float)(Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c) + 1);
    }

    /**
     * Applies an elastic ease-in-out transformation with spring effects at both ends.
     *
     * @since 0.0.10
     */
    public static float inOutElastic(@Range(from = 0, to = 1) float t) {
        double c = TAU / 4.5;

        if (t == 0) return 0;
        if (t == 1) return 1;
        if (t < 0.5) {
            return (float)(-(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * c)) / 2);
        }
        return (float)((Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * c)) / 2 + 1);
    }
    //endregion


    //region Bounce

    /**
     * Applies a bounce-based ease-in transformation.
     *
     * @since 0.0.10
     */
    public static float inBounce(@Range(from = 0, to = 1) float t) {
        return 1 - outBounce(1 - t);
    }

    /**
     * Applies a bounce-based ease-out transformation.
     *
     * @since 0.0.10
     */
    public static float outBounce(@Range(from = 0, to = 1) float t) {
        double c1 = 7.5625;
        double c2 = 2.75;

        if (t < 1 / c2) {
            return (float)(c1 * t * t);
        } else if (t < 2 / c2) {
            return (float)(c1 * (t -= (float)(1.5 / c2)) * t + 0.75);
        } else if (t < 2.5 / c2) {
            return (float)(c1 * (t -= (float)(2.25 / c2)) * t + 0.9375);
        } else {
            return (float)(c1 * (t -= (float)(2.625 / c2)) * t + 0.984375);
        }
    }

    /**
     * Applies a bounce-based ease-in-out transformation.
     *
     * @since 0.0.10
     */
    public static float inOutBounce(@Range(from = 0, to = 1) float t) {
        if (t < 0.5) return inBounce(t * 2) / 2;
        return (outBounce(t * 2 - 1) + 1) / 2;
    }
    //endregion

}
