/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.wrapper;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;


/**
 * Immutable record representing the coordinates of a chunk in a specific world.
 *
 * @since 0.0.13
 */
public record ChunkCoordinate(
  int x,
  int z,
  World world
) {

    public static ChunkCoordinate fromLocation(Location location) {
        return new ChunkCoordinate(
          location.getBlockX() >> 4,
          location.getBlockZ() >> 4,
          location.getWorld()
        );
    }

    public static ChunkCoordinate fromChunk(Chunk chunk) {
        return new ChunkCoordinate(
          chunk.getX(),
          chunk.getZ(),
          chunk.getWorld()
        );
    }

    public static ChunkCoordinate fromChunk(ChunkAccess chunkAccess) {
        if (!(chunkAccess instanceof LevelChunk levelChunk)) throw new IllegalArgumentException("ChunkAccess must be an instance of LevelChunk");

        return new ChunkCoordinate(
          levelChunk.getPos().x(),
          levelChunk.getPos().z(),
          levelChunk.getLevel().getWorld()
        );
    }


    public int manhattanDistance(ChunkCoordinate other) {
        if (!Objects.equals(this.world, other.world)) throw new IllegalArgumentException("Cannot calculate distance between chunks in different worlds");
        return Math.abs(this.x - other.x) + Math.abs(this.z - other.z);
    }

    public int euclideanDistance(ChunkCoordinate other) {
        if (!Objects.equals(this.world, other.world)) throw new IllegalArgumentException("Cannot calculate distance between chunks in different worlds");
        return (int)Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.z - other.z, 2));
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, z, world);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChunkCoordinate(int x1, int z1, World world1))) return false;
        return this.x == x1 && this.z == z1 && Objects.equals(this.world, world1);
    }

    @Override
    public String toString() {
        return "ChunkCoordinate{x=" + x + ", z=" + z + ", world='" + world + "'}";
    }

}