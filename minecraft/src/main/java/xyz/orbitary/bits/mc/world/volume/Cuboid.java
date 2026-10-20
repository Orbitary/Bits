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
        requirePositive("size", size);
        rotation = new Quaternionf(rotation);
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
        Vec3 local = new Frame(center, rotation).toLocal(point);
        return Math.abs(local.x()) <= size.x() / 2
          && Math.abs(local.y()) <= size.y() / 2
          && Math.abs(local.z()) <= size.z() / 2;
    }

    @Override
    public Bounds bounds() {
        Frame frame = new Frame(center, rotation);
        double hx = frame.boxHalfExtent(0, size.x() / 2, size.y() / 2, size.z() / 2);
        double hy = frame.boxHalfExtent(1, size.x() / 2, size.y() / 2, size.z() / 2);
        double hz = frame.boxHalfExtent(2, size.x() / 2, size.y() / 2, size.z() / 2);
        return new Bounds(
          new Vec3(center.x() - hx, center.y() - hy, center.z() - hz),
          new Vec3(center.x() + hx, center.y() + hy, center.z() + hz)
        );
    }

    static void requirePositive(String name, Vec3 value) {
        if (!(value.x() > 0 && value.y() > 0 && value.z() > 0)) throw new IllegalArgumentException(name + " must be positive: " + value);
    }

}
