/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.With;


@With
public record ChunkPos(
  @JsonProperty(required = true) int x,
  @JsonProperty(required = true) int z
) {

    //region Distance
    public int manhattanDistance(ChunkPos other) {
        return Math.abs(this.x - other.x) + Math.abs(this.z - other.z);
    }

    public int euclideanDistance(ChunkPos other) {
        double dx = this.x - other.x;
        double dz = this.z - other.z;
        return (int)Math.sqrt(dx * dx + dz * dz);
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

    public static ChunkPos fromLong(long packed) {
        return new ChunkPos(xFromLong(packed), zFromLong(packed));
    }
    //endregion

}
