/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location;

import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.mc.world.BlockPos;
import xyz.orbitary.bits.mc.world.ChunkPos;
import xyz.orbitary.bits.mc.world.Vec3;
import xyz.orbitary.bits.mc.world.volume.Volume;
import xyz.orbitary.bits.mc.world.volume.WorldVolume;
import xyz.orbitary.bits.paper.util.runnable.Runnables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static xyz.orbitary.bits.mc.world.volume.VolumeUtils.leaves;


public final class Volumes {
    private Volumes() {}


    //region Blocks

    /**
     * @return the block coordinates whose centres lie inside {@code volume}.
     */
    public static Set<BlockPos> blockLocs(Volume volume) {
        Set<BlockPos> locs = new HashSet<>();
        visitBlocks(volume, locs::add);
        return locs;
    }

    /**
     * @return the blocks in the volume's world that pass {@code filter}. Empty if the world is not loaded.
     */
    public static Set<Block> blocks(WorldVolume worldVolume, Predicate<? super Block> filter) {
        World world = world(worldVolume);
        if (world == null) return Set.of();

        return blockLocs(worldVolume.volume()).stream()
          .map(loc -> world.getBlockAt(loc.x(), loc.y(), loc.z()))
          .filter(filter)
          .collect(Collectors.toSet());
    }

    public static Set<Block> blocks(WorldVolume worldVolume) {
        return blocks(worldVolume, _ -> true);
    }

    /**
     * Loads every chunk the volume touches, then resolves its blocks on the main thread.
     */
    public static CompletableFuture<Set<Block>> blocksAsync(WorldVolume worldVolume) {
        World world = world(worldVolume);
        if (world == null) return CompletableFuture.completedFuture(Set.of());

        Set<BlockPos> locs = blockLocs(worldVolume.volume());
        return loadChunks(world, chunkLocsOf(locs, worldVolume.world())).thenApplyAsync(
          _ -> locs.stream()
            .map(loc -> world.getBlockAt(loc.x(), loc.y(), loc.z()))
            .collect(Collectors.toSet()),
          Runnables::runOnMainThread
        );
    }

    //endregion


    //region Chunks

    /**
     * @return the chunk coordinates, in the volume's world, of every chunk it touches.
     */
    public static Set<ChunkPos> chunkLocs(WorldVolume worldVolume) {
        Set<ChunkPos> chunks = new HashSet<>();
        Volume volume = worldVolume.volume();
        Predicate<Vec3> inside = volume.tester();
        for (Volume leaf : leaves(volume)) {
            Vec3 min = leaf.bounds().min(), max = leaf.bounds().max();
            int minX = (int)Math.floor(min.x()), maxX = (int)Math.ceil(max.x());
            int minY = (int)Math.floor(min.y()), maxY = (int)Math.ceil(max.y());
            int minZ = (int)Math.floor(min.z()), maxZ = (int)Math.ceil(max.z());
            for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
                int fromX = Math.max(minX, chunkX << 4), toX = Math.min(maxX, (chunkX << 4) + 15);
                for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                    int fromZ = Math.max(minZ, chunkZ << 4), toZ = Math.min(maxZ, (chunkZ << 4) + 15);
                    search:
                    for (int x = fromX; x <= toX; x++) {
                        for (int y = minY; y <= maxY; y++) {
                            for (int z = fromZ; z <= toZ; z++) {
                                if (!inside.test(new Vec3(x + 0.5, y + 0.5, z + 0.5))) continue;
                                chunks.add(new ChunkPos(chunkX, chunkZ, worldVolume.world()));
                                break search;
                            }
                        }
                    }
                }
            }
        }
        return chunks;
    }

    /**
     * Loads every chunk the volume touches, synchronously. Empty if the world is not loaded.
     */
    public static Set<Chunk> chunks(WorldVolume worldVolume) {
        World world = world(worldVolume);
        if (world == null) return Set.of();

        return chunkLocs(worldVolume).stream()
          .map(chunk -> world.getChunkAt(chunk.x(), chunk.z()))
          .collect(Collectors.toSet());
    }

    /**
     * Loads every chunk the volume touches, asynchronously. Completes empty if the world is not loaded.
     */
    public static CompletableFuture<Set<Chunk>> chunksAsync(WorldVolume worldVolume) {
        World world = world(worldVolume);
        if (world == null) return CompletableFuture.completedFuture(Set.of());

        return loadChunks(world, chunkLocs(worldVolume));
    }

    //endregion


    //region Helpers

    private static void visitBlocks(Volume volume, Consumer<BlockPos> consumer) {
        Predicate<Vec3> inside = volume.tester();
        for (Volume leaf : leaves(volume)) {
            Vec3 min = leaf.bounds().min(), max = leaf.bounds().max();
            for (int x = (int)Math.floor(min.x()); x <= Math.ceil(max.x()); x++) {
                for (int y = (int)Math.floor(min.y()); y <= Math.ceil(max.y()); y++) {
                    for (int z = (int)Math.floor(min.z()); z <= Math.ceil(max.z()); z++) {
                        if (inside.test(new Vec3(x + 0.5, y + 0.5, z + 0.5))) consumer.accept(new BlockPos(x, y, z));
                    }
                }
            }
        }
    }

    /**
     * @return the chunks touched by {@code locs}, in the world with key {@code world}.
     */
    private static Set<ChunkPos> chunkLocsOf(Set<BlockPos> locs, Key world) {
        Set<ChunkPos> chunks = new HashSet<>();
        for (BlockPos loc : locs) {
            chunks.add(new ChunkPos(loc.x() >> 4, loc.z() >> 4, world));
        }
        return chunks;
    }

    private static CompletableFuture<Set<Chunk>> loadChunks(World world, Set<ChunkPos> chunks) {
        List<CompletableFuture<Chunk>> futures = new ArrayList<>();
        for (ChunkPos chunk : chunks) {
            futures.add(world.getChunkAtAsync(chunk.x(), chunk.z()));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
          .thenApply(_ -> futures.stream().map(CompletableFuture::join).collect(Collectors.toSet()));
    }

    /**
     * @return the loaded Bukkit world for {@code worldVolume}, or {@code null} if it is not loaded.
     */
    private static @Nullable World world(WorldVolume worldVolume) {
        return Bukkit.getWorld(worldVolume.world());
    }

    //endregion

}
