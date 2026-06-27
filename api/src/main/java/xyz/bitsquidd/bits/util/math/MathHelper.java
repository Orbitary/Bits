/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Provides static utility methods for common mathematical operations and orientation handling.
 *
 * @since 0.0.10
 */
public final class MathHelper {
    private MathHelper() {}

    /**
     * Rounds a double value to a specified number of decimal places using {@link RoundingMode#HALF_UP}.
     *
     * @param value  the value to round
     * @param places the number of decimal places to keep, must be non-negative
     *
     * @return the rounded value
     *
     * @throws IllegalArgumentException if places is negative
     * @since 0.0.10
     */
    public static double round(final double value, final int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static double sin(final double period, final double amplitude, final double phase) {
        return amplitude * Math.sin(phase * 2 * Math.PI / period);
    }

    public static double cos(final double period, final double amplitude, final double phase) {
        return amplitude * Math.cos(phase * 2 * Math.PI / period);
    }


    /**
     * Set of static utility functions for manipulating and querying {@link Quaternionf} instances.
     *
     * @since 0.0.10
     */
    public static final class Quaternion {
        public static Vector3f X_AXIS() {
            return new Vector3f(1, 0, 0);
        }

        public static Vector3f Y_AXIS() {
            return new Vector3f(0, 1, 0);
        }

        public static Vector3f Z_AXIS() {
            return new Vector3f(0, 0, 1);
        }

        public static float getXRotation(final Quaternionf quat) {
            return (float)Math.atan2(
              2.0 * (quat.w * quat.x + quat.z * quat.y),
              1.0 - 2.0 * (quat.x * quat.x + quat.y * quat.y)
            );
        }

        public static float getYRotation(final Quaternionf quat) {
            return (float)Math.atan2(
              2.0 * (quat.w * quat.y + quat.x * quat.z),
              1.0 - 2.0 * (quat.y * quat.y + quat.x * quat.x)
            );
        }

        public static float getZRotation(final Quaternionf quat) {
            return (float)Math.atan2(
              2.0 * (quat.w * quat.z + quat.y * quat.x),
              1.0 - 2.0 * (quat.x * quat.x + quat.z * quat.z)
            );
        }


        public static Quaternionf invertX(final Quaternionf quat) {
            return quat.rotationX(-getXRotation(quat));
        }

        public static Quaternionf invertY(final Quaternionf quat) {
            return quat.rotationY(-getYRotation(quat));
        }

        public static Quaternionf invertZ(final Quaternionf quat) {
            return quat.rotationZ(-getZRotation(quat));
        }


    }


}
