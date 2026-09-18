/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable;

import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import xyz.bitsquidd.bits.paper.location.wrapper.BlockLoc;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;
import xyz.bitsquidd.bits.paper.location.wrapper.ChunkCoordinate;
import xyz.bitsquidd.bits.paper.location.wrapper.Locatable;
import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public interface Containable {
    boolean contains(Locatable locatable);

    default boolean contains(Location location) {
        if (location == null) return false;
        if (location.getWorld() == null || !location.getWorld().equals(world())) return false;

        return contains(BlockPos.of(location));
    }


    World world();


    BlockPos center();

    default Location centerLoc() {
        return center().asLocation(world());
    }

    BlockPos min();

    BlockPos max();


    default double xSize() {
        return max().x - min().x;
    }

    default double ySize() {
        return max().y - min().y;
    }

    default double zSize() {
        return max().z - min().z;
    }


    Optional<BlockPos> getRandomLocation();


    //region Blocks

    /**
     * Returns the set of blocks where the center points are contained within this region.
     * <p>
     * Not abstract as it may be optimised by some implementations!
     * This default implementation simply checks all blocks within the bounding box of the region.
     */
    default Set<BlockLoc> getBlockLocs() {
        Set<BlockLoc> blockLocs = new HashSet<>();

        BlockPos min = min();
        BlockPos max = max();

        int minXi = (int)Math.floor(min.x);
        int minYi = (int)Math.floor(min.y);
        int minZi = (int)Math.floor(min.z);
        int maxXi = (int)Math.ceil(max.x);
        int maxYi = (int)Math.ceil(max.y);
        int maxZi = (int)Math.ceil(max.z);


        for (int x = minXi; x <= maxXi; x++) {
            for (int y = minYi; y <= maxYi; y++) {
                for (int z = minZi; z <= maxZi; z++) {
                    if (contains(BlockPos.of(x + 0.5, y + 0.5, z + 0.5))) blockLocs.add(BlockLoc.of(x, y, z));
                }
            }
        }

        return blockLocs;
    }

    default Set<Block> getBlocks() {
        return getBlocks(_ -> true);
    }

    default Set<Block> getBlocks(Predicate<Block> filter) {
        return getBlockLocs().stream()
          .map(loc -> loc.asBlock(world()))
          .filter(filter)
          .collect(Collectors.toSet());
    }

    default CompletableFuture<Set<Block>> getBlocksAsync() {
        return getBlocksAsync(_ -> true);
    }

    default CompletableFuture<Set<Block>> getBlocksAsync(Predicate<BlockData> filter) {
        Set<BlockLoc> blockLocs = getBlockLocs();

        // Group BlockLocs by chunk coordinate
        Map<Long, List<BlockLoc>> byChunk = new HashMap<>();
        for (BlockLoc loc : blockLocs) {
            int chunkX = loc.x() >> 4;
            int chunkZ = loc.z() >> 4;
            long key = ((long)chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
            byChunk.computeIfAbsent(key, _ -> new ArrayList<>()).add(loc);
        }

        // For each chunk, load it async and take a snapshot
        List<CompletableFuture<List<BlockLoc>>> futures = byChunk.values().stream()
          .map(locs -> {
              int chunkX = locs.getFirst().x() >> 4;
              int chunkZ = locs.getFirst().z() >> 4;

              return world().getChunkAtAsync(chunkX, chunkZ)
                .thenApplyAsync(chunk -> {
                    ChunkSnapshot snapshot = chunk.getChunkSnapshot();
                    List<BlockLoc> passing = new ArrayList<>();

                    for (BlockLoc loc : locs) {
                        int lx = loc.x() & 0xF;
                        int lz = loc.z() & 0xF;
                        BlockData data = snapshot.getBlockData(lx, loc.y(), lz);
                        if (filter.test(data)) passing.add(loc);
                    }
                    return passing;
                });
          })
          .toList();

        // Combine all passing locs, then hop back to main thread to get refs
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
          .thenApply(v -> futures.stream()
            .flatMap(f -> f.join().stream())
            .collect(Collectors.toSet())
          )
          .thenApplyAsync(
            passingLocs -> passingLocs.stream()
              .map(loc -> world().getBlockAt(loc.x(), loc.y(), loc.z()))
              .collect(Collectors.toSet()),
            Runnables::runOnMainThread
          );
    }
    //endregion


    //region Chunks

    /**
     * Every chunk holding a block this contains, per {@link #getBlockLocs()}.
     * <p>
     * Generic: optimised in implementations: {@link xyz.bitsquidd.bits.paper.location.containable.region.Region}
     *
     * @since 0.0.28
     */
    default Set<ChunkCoordinate> getChunks() {
        Key worldKey = world().key();
        return getBlockLocs().stream()
          .map(loc -> new ChunkCoordinate(loc.x() >> 4, loc.z() >> 4, worldKey))
          .collect(Collectors.toSet());
    }

    /**
     * Like {@link #getChunks()}, but loads (or fetches, if already loaded) every touched chunk asynchronously.
     *
     * @since 0.0.28
     */
    default CompletableFuture<Set<Chunk>> getChunksAsync() {
        List<CompletableFuture<Chunk>> futures = getChunks().stream()
          .map(coord -> world().getChunkAtAsync(coord.x(), coord.z()))
          .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
          .thenApply(_ -> futures.stream().map(CompletableFuture::join).collect(Collectors.toSet()));
    }
    //endregion

}