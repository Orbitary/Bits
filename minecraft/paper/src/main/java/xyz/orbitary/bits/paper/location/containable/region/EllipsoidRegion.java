/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location.containable.region;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import xyz.orbitary.bits.paper.location.wrapper.BlockPos;
import xyz.orbitary.bits.paper.location.wrapper.Locatable;

import java.util.Objects;
import java.util.Optional;


public final class EllipsoidRegion extends Region {
    private final BlockPos center;
    private final double radiusX;
    private final double radiusY;
    private final double radiusZ;

    @JsonCreator
    public EllipsoidRegion(@JsonProperty("world") World world, @JsonProperty("center") BlockPos center, @JsonProperty("radiusX") double radiusX, @JsonProperty("radiusY") double radiusY, @JsonProperty("radiusZ") double radiusZ) {
        this(world, center, radiusX, radiusY, radiusZ, new Quaternionf());
    }

    /**
     * @since 0.0.26
     **/
    public EllipsoidRegion(World world, BlockPos center, double radiusX, double radiusY, double radiusZ, Quaternionf rotation) {
        super(world, rotation);
        if (radiusX <= 0 || radiusY <= 0 || radiusZ <= 0) throw new IllegalArgumentException("Radii must be positive");
        this.center = center;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
    }

    public EllipsoidRegion(World world, BlockPos center, double radius) {
        this(world, center, radius, radius, radius);
    }

    public EllipsoidRegion(Location center, double radiusX, double radiusY, double radiusZ) {
        this(center.getWorld(), BlockPos.of(center), radiusX, radiusY, radiusZ);
    }

    public EllipsoidRegion(Location center, double radiusX, double radiusY, double radiusZ, Quaternionf rotation) {
        this(center.getWorld(), BlockPos.of(center), radiusX, radiusY, radiusZ, rotation);
    }

    public EllipsoidRegion(Location center, double radius) {
        this(center, radius, radius, radius);
    }

    //region Java Object Overrides
    @Override
    public String toString() {
        return "EllipsoidRegion{center=" + center + ", rx=" + radiusX + ", ry=" + radiusY + ", rz=" + radiusZ + ", rotation=" + rotation + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, center, radiusX, radiusY, radiusZ, rotation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EllipsoidRegion other)) return false;
        return Objects.equals(world, other.world) &&
          Objects.equals(center, other.center) &&
          Double.compare(radiusX, other.radiusX) == 0 &&
          Double.compare(radiusY, other.radiusY) == 0 &&
          Double.compare(radiusZ, other.radiusZ) == 0 &&
          rotation.equals(other.rotation, 1.0e-6f);
    }
    //endregion

    @Override
    public boolean contains(Locatable locatable) {
        if (locatable == null) return false;

        Vector v = locatable.asVector();
        Vector3d local = toLocal(v.getX(), v.getY(), v.getZ(), center.x, center.y, center.z);
        double dx = local.x / radiusX;
        double dy = local.y / radiusY;
        double dz = local.z / radiusZ;
        return (dx * dx) + (dy * dy) + (dz * dz) <= 1.0;
    }

    @Override
    public BlockPos center() {
        return center;
    }

    @Override
    public BlockPos min() {
        return BlockPos.of(center.x - radiusX, center.y - radiusY, center.z - radiusZ);
    }

    @Override
    public BlockPos max() {
        return BlockPos.of(center.x + radiusX, center.y + radiusY, center.z + radiusZ);
    }


    @Override
    public Optional<BlockPos> getRandomLocation() {
        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        double r = Math.cbrt(Math.random());

        double x = r * Math.sin(phi) * Math.cos(theta);
        double y = r * Math.sin(phi) * Math.sin(theta);
        double z = r * Math.cos(phi);

        Vector3d local = new Vector3d(x * radiusX, y * radiusY, z * radiusZ);
        rotation.transform(local);

        return Optional.of(BlockPos.of(center.x + local.x, center.y + local.y, center.z + local.z));
    }

    @Override
    public EllipsoidRegion expand(double x, double y, double z) {
        return new EllipsoidRegion(world, center, radiusX + x, radiusY + y, radiusZ + z, rotation);
    }

    @Override
    public EllipsoidRegion shift(double x, double y, double z) {
        return new EllipsoidRegion(
          world,
          BlockPos.of(center.x + x, center.y + y, center.z + z),
          radiusX, radiusY, radiusZ, rotation
        );
    }

    public BlockPos getCenter() {
        return center;
    }

    public double getRadiusX() {
        return radiusX;
    }

    public double getRadiusY() {
        return radiusY;
    }

    public double getRadiusZ() {
        return radiusZ;
    }

}