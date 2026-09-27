/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.region;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Center;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Corner;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Edge;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.RegionVisualiser;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;
import xyz.bitsquidd.bits.paper.location.wrapper.Locatable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


public final class CylinderRegion extends Region {
    private final BlockPos centerBottom;
    private final double radiusX;
    private final double radiusZ;
    private final double height;

    @JsonCreator
    public CylinderRegion(
      @JsonProperty("world") World world, @JsonProperty("centerBottom") BlockPos centerBottom,
      @JsonProperty("radiusX") double radiusX, @JsonProperty("radiusZ") double radiusZ, @JsonProperty("height") double height
    ) {
        this(world, centerBottom, radiusX, radiusZ, height, new Quaternionf());
    }

    public CylinderRegion(World world, BlockPos centerBottom, double radiusX, double radiusZ, double height, Quaternionf rotation) {
        super(world, rotation);
        if (radiusX <= 0 || radiusZ <= 0) throw new IllegalArgumentException("Radii must be positive");
        if (height <= 0) throw new IllegalArgumentException("Height must be positive");
        this.centerBottom = centerBottom;
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.height = height;
    }

    public CylinderRegion(World world, BlockPos centerBottom, double radius, double height) {
        this(world, centerBottom, radius, radius, height, new Quaternionf());
    }

    public CylinderRegion(Location centerBottom, double radiusX, double radiusZ, double height) {
        this(centerBottom.getWorld(), BlockPos.of(centerBottom), radiusX, radiusZ, height);
    }

    public CylinderRegion(Location centerBottom, double radiusX, double radiusZ, double height, Quaternionf rotation) {
        this(centerBottom.getWorld(), BlockPos.of(centerBottom), radiusX, radiusZ, height, rotation);
    }

    public CylinderRegion(Location centerBottom, double radius, double height) {
        this(centerBottom, radius, radius, height);
    }

    //region Java Object Overrides
    @Override
    public String toString() {
        return "CylinderRegion{centerBottom=" + centerBottom + ", rx=" + radiusX + ", rz=" + radiusZ + ", height=" + height + ", rotation=" + rotation + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, centerBottom, radiusX, radiusZ, height, rotation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CylinderRegion other)) return false;
        return Objects.equals(world, other.world) &&
          Objects.equals(centerBottom, other.centerBottom) &&
          Double.compare(radiusX, other.radiusX) == 0 &&
          Double.compare(radiusZ, other.radiusZ) == 0 &&
          Double.compare(height, other.height) == 0 &&
          rotation.equals(other.rotation, 1.0e-6f);
    }
    //endregion

    @Override
    public boolean contains(Locatable locatable) {
        if (locatable == null) return false;

        Vector v = locatable.asVector();
        BlockPos c = center();
        Vector3d local = toLocal(v.getX(), v.getY(), v.getZ(), c.x, c.y, c.z);

        double dx = local.x / radiusX;
        double dz = local.z / radiusZ;
        return Math.abs(local.y) <= height / 2.0 && (dx * dx + dz * dz) <= 1.0;
    }

    @Override
    public BlockPos center() {
        return BlockPos.of(centerBottom.x, centerBottom.y + height / 2, centerBottom.z);
    }

    @Override
    public BlockPos min() {
        return BlockPos.of(centerBottom.x - radiusX, centerBottom.y, centerBottom.z - radiusZ);
    }

    @Override
    public BlockPos max() {
        return BlockPos.of(centerBottom.x + radiusX, centerBottom.y + height, centerBottom.z + radiusZ);
    }


    @Override
    public Optional<BlockPos> getRandomLocation() {
        double angle = Math.random() * 2 * Math.PI;
        double distance = Math.random();
        double x = distance * Math.cos(angle) * radiusX;
        double z = distance * Math.sin(angle) * radiusZ;
        // Relative to center, not centerBottom - rotation() is defined about center().
        double y = Math.random() * height - height / 2.0;

        Vector3d local = new Vector3d(x, y, z);
        rotation.transform(local);

        BlockPos c = center();
        return Optional.of(BlockPos.of(c.x + local.x, c.y + local.y, c.z + local.z));
    }

    @Override
    public CylinderRegion expand(double x, double y, double z) {
        return new CylinderRegion(
          world,
          BlockPos.of(centerBottom.x, centerBottom.y - y, centerBottom.z),
          radiusX + x,
          radiusZ + z,
          height + y * 2,
          rotation
        );
    }

    @Override
    public CylinderRegion shift(double x, double y, double z) {
        return new CylinderRegion(
          world,
          BlockPos.of(centerBottom.x + x, centerBottom.y + y, centerBottom.z + z),
          radiusX, radiusZ, height, rotation
        );
    }


    public BlockPos getCenterBottom() {
        return centerBottom;
    }

    public double getRadiusX() {
        return radiusX;
    }

    public double getRadiusZ() {
        return radiusZ;
    }

    public double getHeight() {
        return height;
    }


    // TODO: Not rotation-aware
    @Override
    protected Set<RegionVisualiser> createVisualiser() {
        Set<RegionVisualiser> visualisers = new HashSet<>();

        int edges = 8;
        BlockPos c = center();

        for (int i = 0; i < edges; i++) {
            double angle = 2 * Math.PI * i / edges;
            double x = c.x + radiusX * Math.cos(angle);
            double z = c.z + radiusZ * Math.sin(angle);
            visualisers.add(Edge.straight(
              BlockPos.of(x, min().y, z),
              BlockPos.of(x, max().y, z)
            ));
        }

        visualisers.add(Corner.of(BlockPos.of(c.x, min().y, c.z)));
        visualisers.add(Corner.of(BlockPos.of(c.x, max().y, c.z)));
        visualisers.add(Center.of(c));

        return visualisers;
    }

}
