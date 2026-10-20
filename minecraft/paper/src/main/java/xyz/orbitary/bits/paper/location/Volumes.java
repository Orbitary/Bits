/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import xyz.orbitary.bits.mc.world.BlockPos;
import xyz.orbitary.bits.mc.world.ChunkPos;
import xyz.orbitary.bits.mc.world.Vec3;
import xyz.orbitary.bits.mc.world.volume.Volume;
import xyz.orbitary.bits.paper.util.bukkit.runnable.Runnables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static xyz.orbitary.bits.mc.world.volume.VolumeUtils.leaves;


public final class Volumes {
    private Volumes() {}


    //region Blocks
    public static Set<BlockPos> blockLocs(Volume volume) {
        Set<BlockPos> locs = new HashSet<>();
        for (Volume leaf : leaves(volume)) {
            Vec3 min = leaf.bounds().min(), max = leaf.bounds().max();
            for (int x = (int)Math.floor(min.x()); x <= Math.ceil(max.x()); x++) {
                for (int y = (int)Math.floor(min.y()); y <= Math.ceil(max.y()); y++) {
                    for (int z = (int)Math.floor(min.z()); z <= Math.ceil(max.z()); z++) {
                        if (volume.contains(new Vec3(x + 0.5, y + 0.5, z + 0.5))) locs.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return locs;
    }

    public static Set<Block> blocks(World world, Volume volume, Predicate<Block> filter) {
        return blockLocs(volume).stream()
          .map(loc -> world.getBlockAt(loc.x(), loc.y(), loc.z()))
          .filter(filter)
          .collect(Collectors.toSet());
    }

    public static Set<Block> blocks(World world, Volume volume) {
        return blocks(world, volume, _ -> true);
    }

    public static CompletableFuture<Set<Block>> blocksAsync(World world, Volume volume) {
        Set<BlockPos> locs = blockLocs(volume);
        return chunksAsync(world, volume).thenApplyAsync(
          _ -> locs.stream()
            .map(loc -> world.getBlockAt(loc.x(), loc.y(), loc.z()))
            .collect(Collectors.toSet()),
          Runnables::runOnMainThread
        );
    }

    //endregion


    //region Chunks
    public static Set<ChunkPos> chunkLocs(World world, Volume volume) {
        Set<ChunkPos> chunks = new HashSet<>();
        for (BlockPos loc : blockLocs(volume)) {
            chunks.add(new ChunkPos(loc.x() >> 4, loc.z() >> 4, world.key()));
        }
        return chunks;
    }

    public static Set<Chunk> chunks(World world, Volume volume) {
        return chunkLocs(world, volume).stream()
          .map(chunk -> world.getChunkAt(chunk.x(), chunk.z()))
          .collect(Collectors.toSet());
    }

    public static CompletableFuture<Set<Chunk>> chunksAsync(World world, Volume volume) {
        List<CompletableFuture<Chunk>> futures = new ArrayList<>();
        for (ChunkPos chunk : chunkLocs(world, volume)) {
            futures.add(world.getChunkAtAsync(chunk.x(), chunk.z()));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
          .thenApply(_ -> futures.stream().map(CompletableFuture::join).collect(Collectors.toSet()));
    }

    //endregion


}
