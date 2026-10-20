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
 * An oriented elliptic cylinder with its axis along local +Y.
 *
 * @param radiusX the radius along local X
 * @param radiusZ the radius along local Z
 * @param height  the full length along local Y
 */
public record Cylinder(
  @JsonProperty(required = true) Vec3 center,
  @JsonProperty(required = true) Quaternionf rotation,
  @JsonProperty(required = true) double radiusX,
  @JsonProperty(required = true) double radiusZ,
  @JsonProperty(required = true) double height
) implements Volume {
    public Cylinder {
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(rotation, "rotation");
        if (!(radiusX > 0 && radiusZ > 0 && height > 0)) throw new IllegalArgumentException("Cylinder radii and height must be positive");
        rotation = new Quaternionf(rotation);
    }

    public static Cylinder of(Vec3 center, Quaternionf rotation, double radiusX, double radiusZ, double height) {
        return new Cylinder(center, rotation, radiusX, radiusZ, height);
    }

    @Override
    public boolean contains(Vec3 point) {
        Vec3 local = new Frame(center, rotation).toLocal(point);
        if (Math.abs(local.y()) > height / 2) return false;
        double nx = local.x() / radiusX, nz = local.z() / radiusZ;
        return nx * nx + nz * nz <= 1.0;
    }

    @Override
    public Bounds bounds() {
        Frame frame = new Frame(center, rotation);
        double halfHeight = height / 2;
        double hx = frame.cylinderHalfExtent(0, radiusX, radiusZ, halfHeight);
        double hy = frame.cylinderHalfExtent(1, radiusX, radiusZ, halfHeight);
        double hz = frame.cylinderHalfExtent(2, radiusX, radiusZ, halfHeight);
        return new Bounds(
          new Vec3(center.x() - hx, center.y() - hy, center.z() - hz),
          new Vec3(center.x() + hx, center.y() + hy, center.z() + hz)
        );
    }

}
