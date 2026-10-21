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
 * An oriented box.
 *
 * @param size the full extent along each local axis
 */
public record Cuboid(
  @JsonProperty(required = true) Vec3 center,
  @JsonProperty(required = true) Quaternionf rotation,
  @JsonProperty(required = true) Vec3 size
) implements Volume {
    public Cuboid {
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(rotation, "rotation");
        Objects.requireNonNull(size, "size");
        VolumeUtils.requirePositive("size", size.x(), size.y(), size.z());
        rotation = VolumeUtils.requireRotation(rotation);
    }

    public static Cuboid of(Vec3 center, Quaternionf rotation, Vec3 size) {
        return new Cuboid(center, rotation, size);
    }

    public static Cuboid of(Vec3 center, Vec3 size) {
        return new Cuboid(center, new Quaternionf(), size);
    }

    public static Cuboid corners(Vec3 corner1, Vec3 corner2) {
        Vec3 min = Vec3.min(corner1, corner2);
        Vec3 max = Vec3.max(corner1, corner2);

        return new Cuboid(
          new Vec3((min.x() + max.x()) / 2, (min.y() + max.y()) / 2, (min.z() + max.z()) / 2),
          new Quaternionf(),
          new Vec3(max.x() - min.x(), max.y() - min.y(), max.z() - min.z())
        );
    }


    @Override
    public boolean contains(Vec3 point) {
        return tester().test(point);
    }

    @Override
    public Predicate<Vec3> tester() {
        Frame frame = new Frame(center, rotation);
        double hx = size.x() / 2, hy = size.y() / 2, hz = size.z() / 2;
        return point -> {
            Vec3 local = frame.toLocal(point);
            return Math.abs(local.x()) <= hx && Math.abs(local.y()) <= hy && Math.abs(local.z()) <= hz;
        };
    }

    @Override
    public Bounds bounds() {
        Frame frame = new Frame(center, rotation);
        double hx = frame.boxHalfExtent(0, size.x() / 2, size.y() / 2, size.z() / 2);
        double hy = frame.boxHalfExtent(1, size.x() / 2, size.y() / 2, size.z() / 2);
        double hz = frame.boxHalfExtent(2, size.x() / 2, size.y() / 2, size.z() / 2);
        return Bounds.around(center, hx, hy, hz);
    }

}
