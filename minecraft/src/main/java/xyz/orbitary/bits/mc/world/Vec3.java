/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

public record Vec3(
  double x,
  double y,
  double z
) {
    // Adding 0.0 turns -0.0 into 0.0 so equals/hashCode treat them the same.
    public Vec3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) throw new IllegalArgumentException("Vec3 must be finite: " + x + "," + y + "," + z);
        x += 0.0;
        y += 0.0;
        z += 0.0;
    }
}
