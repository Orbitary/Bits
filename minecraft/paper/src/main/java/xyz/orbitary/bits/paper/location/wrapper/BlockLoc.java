/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location.wrapper;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * A basic implementation of an integer position in a 3D space.
 *
 * @param x The x-coordinate of the block position.
 * @param y The y-coordinate of the block position.
 * @param z The z-coordinate of the block position.
 *
 * @since 0.0.11
 */
public record BlockLoc(
  int x,
  int y,
  int z
) implements Locatable {

    //region Static Constructors
    public static final BlockLoc ORIGIN = new BlockLoc(0, 0, 0);

    public static BlockLoc of(int x, int y, int z) {
        return new BlockLoc(x, y, z);
    }

    public static BlockLoc of(Vector vector) {
        return of(
          (int)Math.round(vector.getX()),
          (int)Math.round(vector.getY()),
          (int)Math.round(vector.getZ())
        );
    }

    public static BlockLoc of(Location location) {
        return of(
          (int)Math.round(location.getX()),
          (int)Math.round(location.getY()),
          (int)Math.round(location.getZ())
        );
    }

    public static BlockLoc of(Block block) {
        return of(block.getX(), block.getY(), block.getZ());
    }

    public static BlockLoc of(Locatable locatable) {
        return of(locatable.asVector());
    }
    //endregion


    //region Java Methods
    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockLoc(int x1, int y1, int z1))) return false;

        return this.x == x1
          && this.y == y1
          && this.z == z1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
    //endregion


    //region Convertors
    @Override
    public Location asLocation(World world) {
        return new Location(world, x + 0.5, y + 0.5, z + 0.5);
    }

    @Override
    public Block asBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public Vector asVector() {
        return new Vector(x, y, z);
    }
    //endregion


    //region Getters

    @Override
    public YawAndPitch direction() {
        return YawAndPitch.ZERO;
    }

    //endregion

    //region Math Functionality

    @Override
    public BlockLoc mult(Locatable other) {
        Vector otherVector = other.asVector();

        return new BlockLoc(
          (int)(x * otherVector.getX()),
          (int)(y * otherVector.getY()),
          (int)(z * otherVector.getZ())
        );
    }

    @Override
    public BlockLoc mult(Vector vector) {
        return (BlockLoc)Locatable.super.mult(vector);
    }

    @Override
    public BlockLoc mult(double scalar) {
        return (BlockLoc)Locatable.super.mult(scalar);
    }


    @Override
    public BlockLoc add(Locatable other) {
        Vector newVector = other.asVector().add(asVector());

        return new BlockLoc(
          (int)(newVector.getX()),
          (int)(newVector.getY()),
          (int)(newVector.getZ())
        );
    }

    @Override
    public BlockLoc add(Vector vector) {
        return (BlockLoc)Locatable.super.add(vector);
    }

    @Override
    public BlockLoc add(double x, double y, double z) {
        return (BlockLoc)Locatable.super.add(x, y, z);
    }

    //endregion


    //region Rotation Functionality
    // Note that BlockLocs do not rotate, we simply just return this.
    @Override
    public BlockLoc withYawPitch(YawAndPitch rotation) {
        return this;
    }

    @Override
    public BlockLoc withYaw(float yaw) {
        return this;
    }

    @Override
    public BlockLoc withPitch(float pitch) {
        return this;
    }

    @Override
    public BlockLoc rotate(YawAndPitch yawAndPitch) {
        return this;
    }

    @Override
    public BlockLoc rotateYaw(float yaw) {
        return this;
    }

    @Override
    public BlockLoc rotatePitch(float pitch) {
        return this;
    }

    @Override
    public BlockLoc rotate(Rotation rotation) {
        return this;
    }

    //endregion

}
