/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import xyz.orbitary.bits.mc.world.Vec3;

import java.util.List;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Cuboid.class, name = "cuboid"),
  @JsonSubTypes.Type(value = Cylinder.class, name = "cylinder"),
  @JsonSubTypes.Type(value = Ellipsoid.class, name = "ellipsoid"),
  @JsonSubTypes.Type(value = Union.class, name = "union"),
  @JsonSubTypes.Type(value = Difference.class, name = "difference")
})
public sealed interface Volume permits Cuboid, Cylinder, Ellipsoid, Union, Difference {

    /**
     * @return whether {@code point} lies inside or on the surface of this volume.
     */
    boolean contains(Vec3 point);

    /**
     * @return the axis-aligned box that encloses this volume.
     */
    Bounds bounds();

    /**
     * @return the centre of this volume. Shapes return their own centre. Unions and differences return the middle of
     * their bounds.
     */
    default Vec3 center() {
        Bounds bounds = bounds();
        return new Vec3(
          (bounds.min().x() + bounds.max().x()) / 2,
          (bounds.min().y() + bounds.max().y()) / 2,
          (bounds.min().z() + bounds.max().z()) / 2
        );
    }


    default Volume union(Volume other) {
        return new Union(List.of(this, other));
    }

    default Volume cut(Volume other) {
        return new Difference(this, other);
    }

}
