/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import xyz.bitsquidd.bits.paper.location.Locations;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Center;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Corner;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Edge;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.RegionVisualiser;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;
import xyz.bitsquidd.bits.paper.location.wrapper.Locatable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class CuboidRegion extends Region {
    private final BlockPos min;
    private final BlockPos max;

    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public CuboidRegion(World world, BlockPos corner1, BlockPos corner2) {
        super(world);

        this.min = BlockPos.of(Locations.getMinLocation(List.of(corner1, corner2)));
        this.max = BlockPos.of(Locations.getMaxLocation(List.of(corner1, corner2)));

        this.minX = min.x;
        this.minY = min.y;
        this.minZ = min.z;
        this.maxX = max.x;
        this.maxY = max.y;
        this.maxZ = max.z;
    }

    public CuboidRegion(Location corner1, Location corner2) {
        this(corner1.getWorld(), BlockPos.of(corner1), BlockPos.of(corner2));
        if (corner1.getWorld() != corner2.getWorld()) throw new IllegalArgumentException("Both corners must be in the same world");
    }

    //region Java Object Overrides
    @Override
    public String toString() {
        return "CubeRegion{" + min + " -> " + max + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, min, max);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CuboidRegion other)) return false;

        return Objects.equals(world, other.world) &&
          Objects.equals(min, other.min) &&
          Objects.equals(max, other.max);
    }//endregion


    @Override
    public boolean contains(Locatable locatable) {
        if (locatable == null) return false;

        Vector vector = locatable.asVector();

        return vector.getX() >= minX && vector.getX() <= maxX
          && vector.getY() >= minY && vector.getY() <= maxY
          && vector.getZ() >= minZ && vector.getZ() <= maxZ;
    }

    @Override
    public BlockPos center() {
        return BlockPos.of(
          (minX + maxX) / 2,
          (minY + maxY) / 2,
          (minZ + maxZ) / 2
        );
    }

    @Override
    public BlockPos min() {
        return min;
    }

    @Override
    public BlockPos max() {
        return max;
    }

    @Override
    public CuboidRegion expand(double x, double y, double z) {
        return new CuboidRegion(
          world,
          BlockPos.of(minX - x, minY - y, minZ - z),
          BlockPos.of(maxX + x, maxY + y, maxZ + z)
        );
    }

    @Override
    public CuboidRegion shift(double x, double y, double z) {
        return new CuboidRegion(
          world,
          BlockPos.of(minX + x, minY + y, minZ + z),
          BlockPos.of(maxX + x, maxY + y, maxZ + z)
        );
    }


    @Override
    protected Set<RegionVisualiser> createVisualiser() {
        return Set.of(
          Edge.straight(BlockPos.of(min.x, min.y, min.z), BlockPos.of(max.x, min.y, min.z)),
          Edge.straight(BlockPos.of(min.x, min.y, min.z), BlockPos.of(min.x, max.y, min.z)),
          Edge.straight(BlockPos.of(min.x, min.y, min.z), BlockPos.of(min.x, min.y, max.z)),
          Edge.straight(BlockPos.of(max.x, max.y, max.z), BlockPos.of(min.x, max.y, max.z)),
          Edge.straight(BlockPos.of(max.x, max.y, max.z), BlockPos.of(max.x, min.y, max.z)),
          Edge.straight(BlockPos.of(max.x, max.y, max.z), BlockPos.of(max.x, max.y, min.z)),
          Edge.straight(BlockPos.of(min.x, max.y, min.z), BlockPos.of(min.x, max.y, max.z)),
          Edge.straight(BlockPos.of(min.x, max.y, min.z), BlockPos.of(max.x, max.y, min.z)),
          Edge.straight(BlockPos.of(max.x, min.y, min.z), BlockPos.of(max.x, min.y, max.z)),
          Edge.straight(BlockPos.of(max.x, min.y, min.z), BlockPos.of(max.x, max.y, min.z)),
          Edge.straight(BlockPos.of(min.x, min.y, max.z), BlockPos.of(max.x, min.y, max.z)),
          Edge.straight(BlockPos.of(min.x, min.y, max.z), BlockPos.of(min.x, max.y, max.z)),

          Corner.of(BlockPos.of(min.x, min.y, min.z)),
          Corner.of(BlockPos.of(min.x, min.y, max.z)),
          Corner.of(BlockPos.of(min.x, max.y, min.z)),
          Corner.of(BlockPos.of(min.x, max.y, max.z)),
          Corner.of(BlockPos.of(max.x, min.y, min.z)),
          Corner.of(BlockPos.of(max.x, min.y, max.z)),
          Corner.of(BlockPos.of(max.x, max.y, min.z)),
          Corner.of(BlockPos.of(max.x, max.y, max.z)),

          Center.of(center())
        );
    }

}
