/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import xyz.orbitary.bits.util.math.Constants;


public record Vec3(
  double x,
  double y,
  double z
) {
    public static final Vec3 ZERO = new Vec3(0, 0, 0);

    // Adding 0.0 turns -0.0 into 0.0 so equals/hashCode treat them the same.
    public Vec3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) throw new IllegalArgumentException("Vec3 must be finite: " + x + "," + y + "," + z);
        x += 0.0;
        y += 0.0;
        z += 0.0;
    }


    //region Arithmetic
    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 scale(double scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public Vec3 negate() {
        return scale(-1);
    }
    //endregion


    //region Products
    public double dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
          y * other.z - z * other.y,
          z * other.x - x * other.z,
          x * other.y - y * other.x
        );
    }
    //endregion


    //region Length & Distance
    public double lengthSquared() {
        return dot(this);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vec3 normalize() {
        double length = length();
        return length == 0 ? ZERO : scale(1 / length);
    }

    public double distanceSquared(Vec3 other) {
        return subtract(other).lengthSquared();
    }

    public double distance(Vec3 other) {
        return subtract(other).length();
    }

    public boolean isCloseTo(Vec3 other) {
        return distanceSquared(other) <= Constants.EPSILON * Constants.EPSILON;
    }
    //endregion


    //region Conversions
    public BlockPos toBlockPos() {
        return new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }
    //endregion

}
