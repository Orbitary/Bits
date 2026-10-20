/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;

import xyz.orbitary.bits.util.math.Constants;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.DoubleBinaryOperator;


public record Vec3(
  @JsonProperty(required = true) double x,
  @JsonProperty(required = true) double y,
  @JsonProperty(required = true) double z
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

    public Vec3 add(double dx, double dy, double dz) {
        return new Vec3(x + dx, y + dy, z + dz);
    }

    public Vec3 negate() {
        return scale(-1);
    }
    //endregion


    //region Aggregates
    public static Vec3 midpoint(Collection<Vec3> vectors) {
        requireNotEmpty(vectors);
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        for (Vec3 vector : vectors) {
            sumX += vector.x;
            sumY += vector.y;
            sumZ += vector.z;
        }
        int count = vectors.size();
        return new Vec3(sumX / count, sumY / count, sumZ / count);
    }

    public static Vec3 min(Collection<Vec3> vectors) {
        return reduce(vectors, Math::min);
    }

    public static Vec3 max(Collection<Vec3> vectors) {
        return reduce(vectors, Math::max);
    }

    private static Vec3 reduce(Collection<Vec3> vectors, DoubleBinaryOperator operator) {
        requireNotEmpty(vectors);
        Iterator<Vec3> iterator = vectors.iterator();
        Vec3 first = iterator.next();
        double x = first.x;
        double y = first.y;
        double z = first.z;
        while (iterator.hasNext()) {
            Vec3 vector = iterator.next();
            x = operator.applyAsDouble(x, vector.x);
            y = operator.applyAsDouble(y, vector.y);
            z = operator.applyAsDouble(z, vector.z);
        }
        return new Vec3(x, y, z);
    }

    private static void requireNotEmpty(Collection<Vec3> vectors) {
        if (vectors.isEmpty()) throw new IllegalArgumentException("Vectors cannot be empty");
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
        return distanceSquared(x, y, z, other.x, other.y, other.z);
    }

    public double distance(Vec3 other) {
        return distance(x, y, z, other.x, other.y, other.z);
    }

    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    public boolean isCloseTo(Vec3 other) {
        return distanceSquared(other) <= Constants.EPSILON * Constants.EPSILON;
    }

    public boolean isWithinDistance(Vec3 other, double distance) {
        return distanceSquared(other) <= distance * distance;
    }
    //endregion


    //region Conversions
    public BlockPos toBlockPos() {
        return new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }
    //endregion

}
