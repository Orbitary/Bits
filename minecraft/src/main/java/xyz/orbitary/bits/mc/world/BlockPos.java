/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.With;
import net.kyori.adventure.key.Key;

import xyz.orbitary.bits.data.world.Cardinal;


@With
public record BlockPos(
  @JsonProperty(required = true) int x,
  @JsonProperty(required = true) int y,
  @JsonProperty(required = true) int z
) {
    public static final BlockPos ZERO = new BlockPos(0, 0, 0);


    //region Arithmetic
    public BlockPos add(BlockPos other) {
        return new BlockPos(x + other.x, y + other.y, z + other.z);
    }

    public BlockPos add(int dx, int dy, int dz) {
        return new BlockPos(x + dx, y + dy, z + dz);
    }

    public BlockPos subtract(BlockPos other) {
        return new BlockPos(x - other.x, y - other.y, z - other.z);
    }

    public BlockPos scale(int scalar) {
        return new BlockPos(x * scalar, y * scalar, z * scalar);
    }

    public BlockPos offset(Cardinal cardinal) {
        return offset(cardinal, 1);
    }

    public BlockPos offset(Cardinal cardinal, int amount) {
        return new BlockPos(
          x + cardinal.dx() * amount,
          y + cardinal.dy() * amount,
          z + cardinal.dz() * amount
        );
    }
    //endregion


    //region Distance
    public long distanceSquared(BlockPos other) {
        long dx = (long)x - other.x;
        long dy = (long)y - other.y;
        long dz = (long)z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }
    //endregion


    //region Packing
    // Same layout as Mojang: x (26 bits) | z (26 bits) | y (12 bits). Values outside x/z +-33,554,432 or y [-2048, 2047] wrap silently.
    private static final long XZ_MASK = (1L << 26) - 1;
    private static final long Y_MASK = (1L << 12) - 1;
    private static final int X_SHIFT = 38;
    private static final int Z_SHIFT = 12;

    public long asLong() {
        return asLong(x, y, z);
    }

    public static long asLong(int x, int y, int z) {
        return ((x & XZ_MASK) << X_SHIFT) | ((z & XZ_MASK) << Z_SHIFT) | (y & Y_MASK);
    }

    public static int xFromLong(long packed) {
        return (int)(packed >> X_SHIFT);
    }

    public static int yFromLong(long packed) {
        return (int)((packed << 52) >> 52);
    }

    public static int zFromLong(long packed) {
        return (int)((packed << 26) >> X_SHIFT);
    }

    public static BlockPos fromLong(long packed) {
        return new BlockPos(xFromLong(packed), yFromLong(packed), zFromLong(packed));
    }
    //endregion


    //region Conversions
    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public Vec3 center() {
        return new Vec3(x + 0.5, y + 0.5, z + 0.5);
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(x >> 4, z >> 4);
    }

    public Coord toCoords(Key world) {
        return new Coord.Record(world, new Pos(center(), Rotation.ZERO));
    }
    //endregion

}
