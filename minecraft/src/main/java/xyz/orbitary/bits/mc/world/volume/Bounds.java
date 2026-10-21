/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import com.fasterxml.jackson.annotation.JsonProperty;

import xyz.orbitary.bits.mc.world.Vec3;


/**
 * An axis-aligned box, inclusive of both corners.
 */
public record Bounds(
  @JsonProperty(required = true) Vec3 min,
  @JsonProperty(required = true) Vec3 max
) {
    public Bounds {
        if (min.x() > max.x() || min.y() > max.y() || min.z() > max.z()) throw new IllegalArgumentException("min must not exceed max: " + min + ", " + max);
    }

    /**
     * @return whether {@code point} lies inside or on the boundary of this box.
     */
    public boolean contains(Vec3 point) {
        return point.x() >= min.x() && point.x() <= max.x()
          && point.y() >= min.y() && point.y() <= max.y()
          && point.z() >= min.z() && point.z() <= max.z();
    }

    /**
     * @return the box centred on {@code center}, extending {@code hx}, {@code hy} and {@code hz} from it on each axis.
     */
    static Bounds around(Vec3 center, double hx, double hy, double hz) {
        return new Bounds(
          new Vec3(center.x() - hx, center.y() - hy, center.z() - hz),
          new Vec3(center.x() + hx, center.y() + hy, center.z() + hz)
        );
    }

    /**
     * @return the smallest box enclosing both this box and {@code other}.
     */
    public Bounds merge(Bounds other) {
        return new Bounds(
          new Vec3(Math.min(min.x(), other.min.x()), Math.min(min.y(), other.min.y()), Math.min(min.z(), other.min.z())),
          new Vec3(Math.max(max.x(), other.max.x()), Math.max(max.y(), other.max.y()), Math.max(max.z(), other.max.z()))
        );
    }
}
