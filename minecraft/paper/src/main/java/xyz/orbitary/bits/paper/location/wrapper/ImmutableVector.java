/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location.wrapper;

import org.bukkit.util.Vector;


/**
 * Immutable representation of a Bukkit {@link Vector}.
 *
 * @since 0.0.22
 * @deprecated Use {@link xyz.orbitary.bits.mc.world.Vec3}, converted with {@link xyz.orbitary.bits.paper.location.Locations}.
 */
@Deprecated(forRemoval = true, since = "0.1.1")
public record ImmutableVector(
  double x,
  double y,
  double z
) {
    public ImmutableVector() {
        this(0, 0, 0);
    }

    public static ImmutableVector of(Vector vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector toBukkit() {
        return new Vector(x, y, z);
    }

}