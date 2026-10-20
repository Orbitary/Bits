/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import net.kyori.adventure.key.Key;

import java.util.Objects;


public record ChunkPos(
  int x,
  int z,
  Key world
) {
    public ChunkPos {
        Objects.requireNonNull(world, "world");
    }


    //region Distance
    public int manhattanDistance(ChunkPos other) throws IllegalArgumentException {
        requireSameWorld(other);
        return Math.abs(this.x - other.x) + Math.abs(this.z - other.z);
    }

    public int euclideanDistance(ChunkPos other) throws IllegalArgumentException {
        requireSameWorld(other);
        return (int)Math.hypot(this.x - other.x, this.z - other.z);
    }

    private void requireSameWorld(ChunkPos other) throws IllegalArgumentException {
        if (!world.equals(other.world)) throw new IllegalArgumentException("Cannot calculate distance between chunks in different worlds");
    }
    //endregion


    //region Java Overrides
    @Override
    public String toString() {
        return "ChunkPos:" + x + "," + z + "," + world;
    }
    //endregion

}
