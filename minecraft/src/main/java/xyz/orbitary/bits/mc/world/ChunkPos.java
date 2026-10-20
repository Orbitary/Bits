/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.key.Key;

import java.util.Objects;


public record ChunkPos(
  @JsonProperty(required = true) int x,
  @JsonProperty(required = true) int z,
  @JsonProperty(required = true) Key world
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
        double dx = this.x - other.x;
        double dz = this.z - other.z;
        return (int)Math.sqrt(dx * dx + dz * dz);
    }

    private void requireSameWorld(ChunkPos other) throws IllegalArgumentException {
        if (!world.equals(other.world)) throw new IllegalArgumentException("Cannot calculate distance between chunks in different worlds");
    }
    //endregion


    //region Packing
    public long asLong() {
        return asLong(x, z);
    }

    public static long asLong(int x, int z) {
        return (x & 0xFFFFFFFFL) | ((long)z << 32);
    }

    public static int xFromLong(long packed) {
        return (int)packed;
    }

    public static int zFromLong(long packed) {
        return (int)(packed >>> 32);
    }

    public static ChunkPos fromLong(long packed, Key world) {
        return new ChunkPos(xFromLong(packed), zFromLong(packed), world);
    }
    //endregion


    //region Java Overrides
    @Override
    public String toString() {
        return "ChunkPos:" + x + "," + z + "," + world;
    }
    //endregion

}
