/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import net.kyori.adventure.key.Key;

import xyz.orbitary.bits.data.world.Cardinal;


public record BlockPos(
  int x,
  int y,
  int z
) {
    public static final BlockPos ZERO = new BlockPos(0, 0, 0);


    //region Arithmetic
    public BlockPos add(BlockPos other) {
        return new BlockPos(x + other.x, y + other.y, z + other.z);
    }

    public BlockPos subtract(BlockPos other) {
        return new BlockPos(x - other.x, y - other.y, z - other.z);
    }

    public BlockPos offset(Cardinal cardinal) {
        return offset(cardinal, 1);
    }

    public BlockPos offset(Cardinal cardinal, int amount) {
        return new BlockPos(
          x + cardinal.unitVector[0] * amount,
          y + cardinal.unitVector[1] * amount,
          z + cardinal.unitVector[2] * amount
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


    //region Conversions
    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public Vec3 center() {
        return new Vec3(x + 0.5, y + 0.5, z + 0.5);
    }

    public ChunkPos toChunkPos(Key world) {
        return new ChunkPos(x >> 4, z >> 4, world);
    }

    public Location toLocation(Key world) {
        return new Location(world, new Position(center(), Rotation.ZERO));
    }
    //endregion

}
