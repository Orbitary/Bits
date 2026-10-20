/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joml.Quaternionf;

import xyz.orbitary.bits.mc.world.Vec3;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Cuboid.class, name = "cuboid"),
  @JsonSubTypes.Type(value = Cylinder.class, name = "cylinder"),
  @JsonSubTypes.Type(value = Ellipsoid.class, name = "ellipsoid")
})
public sealed interface Volume permits Cuboid, Cylinder, Ellipsoid {

    /**
     * @return whether {@code point} lies inside or on the surface of this volume.
     */
    boolean contains(Vec3 point);

    /**
     * @return the axis-aligned box that encloses this volume.
     */
    Bounds bounds();

    Vec3 center();

    Quaternionf rotation();

}
