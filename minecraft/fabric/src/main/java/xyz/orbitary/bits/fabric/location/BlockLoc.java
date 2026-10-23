/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.fabric.location;

import net.kyori.adventure.key.Key;
import net.minecraft.world.level.Level;

import xyz.orbitary.bits.mc.world.BlockCoord;
import xyz.orbitary.bits.mc.world.BlockPos;

import java.util.Objects;


public record BlockLoc(
  Level level,
  int x,
  int y,
  int z
) implements BlockCoord {
    public BlockLoc {
        Objects.requireNonNull(level, "level");
    }


    @Override
    public Key world() {
        return Locations.worldKey(level);
    }

    @Override
    public BlockPos pos() {
        return new BlockPos(x, y, z);
    }

}
