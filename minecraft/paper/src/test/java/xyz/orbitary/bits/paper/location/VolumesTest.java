/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location;

import net.kyori.adventure.key.Key;
import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

import xyz.orbitary.bits.mc.world.BlockPos;
import xyz.orbitary.bits.mc.world.ChunkCoord;
import xyz.orbitary.bits.mc.world.ChunkPos;
import xyz.orbitary.bits.mc.world.Vec3;
import xyz.orbitary.bits.mc.world.volume.Cuboid;
import xyz.orbitary.bits.mc.world.volume.Cylinder;
import xyz.orbitary.bits.mc.world.volume.Ellipsoid;
import xyz.orbitary.bits.mc.world.volume.Volume;
import xyz.orbitary.bits.mc.world.volume.WorldVolume;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class VolumesTest {
    private static final Key WORLD = Key.key("test:world");

    @Test
    void blockCentersOnTheSurfaceAreIncludedAndOverlapsAreDeduplicated() {
        Volume cube = Cuboid.of(Vec3.ZERO, new Vec3(1, 1, 1));
        assertEquals(
          Set.of(
            new BlockPos(-1, -1, -1), new BlockPos(-1, -1, 0),
            new BlockPos(-1, 0, -1), new BlockPos(-1, 0, 0),
            new BlockPos(0, -1, -1), new BlockPos(0, -1, 0),
            new BlockPos(0, 0, -1), new BlockPos(0, 0, 0)
          ), Volumes.blockLocs(cube.union(cube))
        );
        assertEquals(
          Set.of(
            chunk(-1, -1), chunk(-1, 0),
            chunk(0, -1), chunk(0, 0)
          ), Volumes.chunkLocs(WorldVolume.of(WORLD, cube.union(cube)))
        );
    }

    @Test
    void negativeCoordinatesUseFloorDivisionAtChunkBoundaries() {
        Volume cube = Cuboid.corners(new Vec3(-17, 0, -1), new Vec3(1, 2, 17));
        assertEquals(
          Set.of(
            chunk(-2, -1), chunk(-2, 0), chunk(-2, 1),
            chunk(-1, -1), chunk(-1, 0), chunk(-1, 1),
            chunk(0, -1), chunk(0, 0), chunk(0, 1)
          ), Volumes.chunkLocs(WorldVolume.of(WORLD, cube))
        );
    }

    @Test
    void removingTheEntireShapeProducesEmptyMutableSnapshots() {
        Volume cube = Cuboid.of(Vec3.ZERO, new Vec3(2, 2, 2));
        Volume empty = cube.cut(cube);
        Set<BlockPos> blocks = Volumes.blockLocs(empty);
        Set<ChunkCoord> chunks = Volumes.chunkLocs(WorldVolume.of(WORLD, empty));
        assertTrue(blocks.isEmpty());
        assertTrue(chunks.isEmpty());
        blocks.add(BlockPos.ZERO);
        chunks.add(chunk(0, 0));
        assertTrue(Volumes.blockLocs(empty).isEmpty());
        assertTrue(Volumes.chunkLocs(WorldVolume.of(WORLD, empty)).isEmpty());
    }

    @Test
    void chunkCoordinatesAgreeWithContainedBlocksForRotatedCompositeVolumes() {
        Random random = new Random(713);
        for (int i = 0; i < 60; i++) {
            Vec3 center = new Vec3(random.nextInt(40) - 20, 0, random.nextInt(40) - 20);
            Quaternionf rotation = new Quaternionf().rotateXYZ(random.nextFloat(), random.nextFloat(), random.nextFloat());
            Volume shape = switch (i % 3) {
                case 0 -> Cuboid.of(center, rotation, new Vec3(4, 5, 6));
                case 1 -> Cylinder.of(center, rotation, 3, 2, 5);
                default -> Ellipsoid.of(center, rotation, new Vec3(4, 2, 3));
            };
            for (Volume volume : List.of(shape, shape.union(shape).cut(Cuboid.of(center, new Vec3(2, 3, 2))))) {
                Set<ChunkCoord> expected = Volumes.blockLocs(volume).stream()
                  .map(block -> new ChunkCoord.Record(WORLD, block.toChunkPos()))
                  .collect(Collectors.toSet());
                assertEquals(expected, Volumes.chunkLocs(WorldVolume.of(WORLD, volume)));
            }
        }
    }

    private static ChunkCoord chunk(int x, int z) {
        return new ChunkCoord.Record(WORLD, new ChunkPos(x, z));
    }

}
