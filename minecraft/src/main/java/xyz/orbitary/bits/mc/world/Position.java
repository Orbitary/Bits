/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import java.util.Objects;


public record Position(
  Vec3 pos,
  Rotation rot
) {
    public Position {
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(rot, "rot");
    }
}
