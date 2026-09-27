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

import xyz.orbitary.bits.data.world.Cardinal;

import java.util.Objects;

/**
 * Represents an integer block position with an associated cardinal direction.
 *
 * @param x        The x-coordinate of the block position.
 * @param y        The y-coordinate of the block position.
 * @param z        The z-coordinate of the block position.
 * @param cardinal The cardinal direction associated with this block position.
 *
 * @since 0.0.11
 */
public record BlockCardinal(
  int x,
  int y,
  int z,
  Cardinal cardinal
) implements Locatable {

    //region Static Constructors
    public static final BlockCardinal ORIGIN = new BlockCardinal(0, 0, 0, Cardinal.NORTH);

    public static BlockCardinal of(int x, int y, int z, Cardinal cardinal) {
        return new BlockCardinal(x, y, z, cardinal);
    }

    public static BlockCardinal of(int x, int y, int z) {
        return of(x, y, z, Cardinal.NORTH);
    }

    public static BlockCardinal of(Vector vector) {
        return of(
          (int)Math.round(vector.getX()),
          (int)Math.round(vector.getY()),
          (int)Math.round(vector.getZ())
        );
    }

    public static BlockCardinal of(Location location) {
        return of(
          (int)Math.round(location.getX()),
          (int)Math.round(location.getY()),
          (int)Math.round(location.getZ()),
          YawAndPitch.from(location).toCardinal()
        );
    }

    public static BlockCardinal of(Block block) {
        return of(block.getLocation());
    }

    public static BlockCardinal of(Locatable locatable) {
        return of(locatable.asVector());
    }
    //endregion

    //region Java Methods
    @Override
    public String toString() {
        return x + ", " + y + ", " + z + ", " + cardinal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockCardinal(int x1, int y1, int z1, Cardinal cardinal1))) return false;

        return this.x == x1
          && this.y == y1
          && this.z == z1
          && this.cardinal == cardinal1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, cardinal);
    }
    //endregion


    //region Convertors
    @Override
    public Location asLocation(World world) {
        YawAndPitch yawAndPitch = YawAndPitch.from(cardinal);
        return new Location(world, x + 0.5, y + 0.5, z + 0.5, yawAndPitch.yaw, yawAndPitch.pitch);
    }

    @Override
    public Block asBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public Vector asVector() {
        return new Vector(x, y, z);
    }

    @Override
    public YawAndPitch direction() {
        return YawAndPitch.from(cardinal);
    }
    //endregion


    //region Math Functionality

    @Override
    public BlockCardinal mult(Locatable other) {
        Vector otherVector = other.asVector();
        YawAndPitch newDirection = direction(); // Not too sure what yaw and pitch should be when multiplying.

        return new BlockCardinal(
          (int)(x * otherVector.getX()),
          (int)(y * otherVector.getY()),
          (int)(z * otherVector.getZ()),
          newDirection.toCardinal()
        );
    }

    @Override
    public BlockCardinal mult(Vector vector) {
        return (BlockCardinal)Locatable.super.mult(vector);
    }

    @Override
    public BlockCardinal mult(double scalar) {
        return (BlockCardinal)Locatable.super.mult(scalar);
    }


    @Override
    public BlockCardinal add(Locatable other) {
        Vector newVector = other.asVector().add(asVector());

        return new BlockCardinal(
          (int)(newVector.getX()),
          (int)(newVector.getY()),
          (int)(newVector.getZ()),
          cardinal // Note: we do not add direction here.
        );
    }

    @Override
    public BlockCardinal add(Vector vector) {
        return (BlockCardinal)Locatable.super.add(vector);
    }

    @Override
    public BlockCardinal add(double x, double y, double z) {
        return (BlockCardinal)Locatable.super.add(x, y, z);
    }

    //endregion


    //region Rotation Functionality
    @Override
    public BlockCardinal withYawPitch(YawAndPitch rotation) {
        return new BlockCardinal(this.x, this.y, this.z, rotation.toCardinal());
    }

    @Override
    public BlockCardinal withYaw(float yaw) {
        return (BlockCardinal)Locatable.super.withYaw(yaw);
    }

    @Override
    public BlockCardinal withPitch(float pitch) {
        return (BlockCardinal)Locatable.super.withPitch(pitch);
    }

    @Override
    public BlockCardinal rotate(YawAndPitch yawAndPitch) {
        return new BlockCardinal(this.x, this.y, this.z, direction().add(yawAndPitch).toCardinal());
    }

    @Override
    public BlockCardinal rotateYaw(float yaw) {
        return (BlockCardinal)Locatable.super.rotateYaw(yaw);
    }

    @Override
    public BlockCardinal rotatePitch(float pitch) {
        return (BlockCardinal)Locatable.super.rotatePitch(pitch);
    }

    @Override
    public BlockCardinal rotate(Rotation rotation) {
        return (BlockCardinal)Locatable.super.rotate(rotation);
    }
    //endregion


    //region BlockCardinal-specific Functionality
    public BlockCardinal flip() {
        return new BlockCardinal(x, y, z, cardinal.flip());
    }
    //endregion
}
