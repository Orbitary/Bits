/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.wrapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Objects;


/**
 * Immutable class representing a position in the world with x, y, z coordinates and yaw, pitch rotation.
 */
public final class BlockPos implements Locatable {
    public final double x;
    public final double y;
    public final double z;

    public final float yaw;
    public final float pitch;

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0, 0, 0);

    @JsonCreator
    private BlockPos(
      @JsonProperty("x") double x,
      @JsonProperty("y") double y,
      @JsonProperty("z") double z,
      @JsonProperty("yaw") float yaw,
      @JsonProperty("pitch") float pitch
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = (yaw + 180) % 360 - 180;   // Normalizes yaw to   [-180, 180)
        this.pitch = (pitch + 90) % 180 - 90; // Normalizes pitch to [-90, 90]
    }

    //region Static Constructors
    public static BlockPos of(double x, double y, double z, float yaw, float pitch) {
        return new BlockPos(x, y, z, yaw, pitch);
    }

    public static BlockPos of(double x, double y, double z) {
        return of(x, y, z, 0, 0);
    }

    public static BlockPos of(Location location) {
        return of(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static BlockPos of(Entity entity) {
        return of(entity.getLocation());
    }

    public static BlockPos of(Block block) {
        return of(block.getLocation().toCenterLocation());
    }

    public static BlockPos of(Locatable locatable) {
        return of(locatable.asVector().getX(), locatable.asVector().getY(), locatable.asVector().getZ(), locatable.direction().yaw, locatable.direction().pitch);
    }

    public static BlockPos fromString(String str) {
        String trimmed = str.trim();
        String[] parts = trimmed.split(",");
        if (parts.length != 5 && parts.length != 3) throw new IllegalArgumentException("Invalid BlockPos string: " + str);
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        if (parts.length == 5) {
            float yaw = Float.parseFloat(parts[3]);
            float pitch = Float.parseFloat(parts[4]);
            return new BlockPos(x, y, z, yaw, pitch);
        } else {
            return new BlockPos(x, y, z, 0, 0);
        }
    }

    //endregion


    //region Java Methods
    @Override
    public String toString() {
        return df.format(x) + "," + df.format(y) + "," + df.format(z) + "," + df.format(yaw) + "," + df.format(pitch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockPos other)) return false;

        return this.x == other.x
          && this.y == other.y
          && this.z == other.z
          && this.yaw == other.yaw
          && this.pitch == other.pitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, yaw, pitch);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public BlockPos clone() {
        return new BlockPos(x, y, z, yaw, pitch);
    }
    //endregion


    //region Convertors
    @Override
    public Location asLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public Block asBlock(World world) {
        return world.getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }

    @Override
    public Vector asVector() {
        return new Vector(x, y, z);
    }
    //endregion


    //region Getters

    @Override
    public YawAndPitch direction() {
        return YawAndPitch.of(yaw, pitch);
    }

    //endregion

    //region Math Functionality

    @Override
    public BlockPos mult(Locatable other) {
        Vector otherVector = other.asVector();
        YawAndPitch newDirection = direction(); // Not too sure what yaw and pitch should be when multiplying.

        return new BlockPos(
          (int)(x * otherVector.getX()),
          (int)(y * otherVector.getY()),
          (int)(z * otherVector.getZ()),
          newDirection.yaw,
          newDirection.pitch
        );
    }

    @Override
    public BlockPos mult(Vector vector) {
        return (BlockPos)Locatable.super.mult(vector);
    }

    @Override
    public BlockPos mult(double scalar) {
        return (BlockPos)Locatable.super.mult(scalar);
    }


    @Override
    public BlockPos add(Locatable other) {
        Vector newVector = other.asVector().add(asVector());

        return new BlockPos(
          newVector.getX(),
          newVector.getY(),
          newVector.getZ(),
          yaw,  // Note: we do not add direction here.
          pitch
        );
    }

    @Override
    public BlockPos add(Vector vector) {
        return (BlockPos)Locatable.super.add(vector);
    }

    @Override
    public BlockPos add(double x, double y, double z) {
        return (BlockPos)Locatable.super.add(x, y, z);
    }

    //endregion


    //region Rotation Functionality
    @Override
    public BlockPos withYawPitch(YawAndPitch rotation) {
        return new BlockPos(this.x, this.y, this.z, rotation.yaw, rotation.pitch);
    }

    @Override
    public BlockPos withYaw(float yaw) {
        return (BlockPos)Locatable.super.withYaw(yaw);
    }

    @Override
    public BlockPos withPitch(float pitch) {
        return (BlockPos)Locatable.super.withPitch(pitch);
    }

    @Override
    public BlockPos rotate(YawAndPitch yawAndPitch) {
        float newYaw = this.yaw + yawAndPitch.yaw;
        float newPitch = this.pitch + yawAndPitch.pitch;
        return new BlockPos(this.x, this.y, this.z, newYaw, newPitch);
    }

    @Override
    public BlockPos lookTowards(Locatable target) {
        return (BlockPos)Locatable.super.lookTowards(target);
    }

    @Override
    public BlockPos rotateYaw(float yaw) {
        return (BlockPos)Locatable.super.rotateYaw(yaw);
    }

    @Override
    public BlockPos rotatePitch(float pitch) {
        return (BlockPos)Locatable.super.rotatePitch(pitch);
    }

    @Override
    public BlockPos rotate(Rotation rotation) {
        return (BlockPos)Locatable.super.rotate(rotation);
    }

    //endregion

}
