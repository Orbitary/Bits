/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Quaternionf;

import xyz.orbitary.bits.mc.world.Vec3;

import java.util.Objects;
import java.util.function.Predicate;


/**
 * An oriented ellipsoid. A sphere is an ellipsoid with equal radii.
 *
 * @param radius the radius along each local axis
 */
public record Ellipsoid(
  @JsonProperty(required = true) Vec3 center,
  @JsonProperty(required = true) Quaternionf rotation,
  @JsonProperty(required = true) Vec3 radius
) implements Volume {
    public Ellipsoid {
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(rotation, "rotation");
        Objects.requireNonNull(radius, "radius");
        VolumeUtils.requirePositive("radius", radius.x(), radius.y(), radius.z());
        rotation = VolumeUtils.requireRotation(rotation);
    }

    public static Ellipsoid of(Vec3 center, Quaternionf rotation, Vec3 radius) {
        return new Ellipsoid(center, rotation, radius);
    }

    public static Ellipsoid of(Vec3 center, Vec3 radius) {
        return new Ellipsoid(center, new Quaternionf(), radius);
    }
    

    @Override
    public boolean contains(Vec3 point) {
        return tester().test(point);
    }

    @Override
    public Predicate<Vec3> tester() {
        Frame frame = new Frame(center, rotation);
        double rx = radius.x(), ry = radius.y(), rz = radius.z();
        return point -> {
            Vec3 local = frame.toLocal(point);
            double nx = local.x() / rx, ny = local.y() / ry, nz = local.z() / rz;
            return nx * nx + ny * ny + nz * nz <= 1.0;
        };
    }

    @Override
    public Bounds bounds() {
        Frame frame = new Frame(center, rotation);
        double hx = frame.ellipsoidHalfExtent(0, radius.x(), radius.y(), radius.z());
        double hy = frame.ellipsoidHalfExtent(1, radius.x(), radius.y(), radius.z());
        double hz = frame.ellipsoidHalfExtent(2, radius.x(), radius.y(), radius.z());
        return Bounds.around(center, hx, hy, hz);
    }

}
