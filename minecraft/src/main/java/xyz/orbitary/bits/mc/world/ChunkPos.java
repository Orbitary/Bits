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

    public int manhattanDistance(ChunkPos other) {
        if (!Objects.equals(this.world, other.world)) throw new IllegalArgumentException("Cannot calculate distance between chunks in different worlds");
        return Math.abs(this.x - other.x) + Math.abs(this.z - other.z);
    }

    public int euclideanDistance(ChunkPos other) {
        if (!Objects.equals(this.world, other.world)) throw new IllegalArgumentException("Cannot calculate distance between chunks in different worlds");
        return (int)Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.z - other.z, 2));
    }


    //region Java Overrides
    @Override
    public String toString() {
        return "ChunkPos:" + x + "," + z + "," + world;
    }
    //endregion

}