/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import net.kyori.adventure.key.Key;

import java.util.Objects;


public record Location(
  Key world,
  Position position
) {
    public Location {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(position, "position");
    }
}
