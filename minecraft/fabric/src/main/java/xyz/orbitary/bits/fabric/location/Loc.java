/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.fabric.location;

import net.kyori.adventure.key.Key;
import net.minecraft.world.level.Level;

import xyz.orbitary.bits.mc.world.Coord;
import xyz.orbitary.bits.mc.world.Pos;
import xyz.orbitary.bits.mc.world.Rotation;
import xyz.orbitary.bits.mc.world.Vec3;

import java.util.Objects;


public record Loc(
  Level level,
  double x,
  double y,
  double z,
  float yaw,
  float pitch
) implements Coord {
    public Loc {
        Objects.requireNonNull(level, "level");
    }


    @Override
    public Key world() {
        return Locations.worldKey(level);
    }

    @Override
    public Pos pos() {
        return new Pos(new Vec3(x, y, z), Rotation.of(yaw, pitch));
    }

}
