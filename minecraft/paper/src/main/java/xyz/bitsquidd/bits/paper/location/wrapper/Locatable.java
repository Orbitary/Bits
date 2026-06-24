/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.wrapper;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import xyz.bitsquidd.bits.data.world.Relative;

import java.text.DecimalFormat;


public interface Locatable {
    DecimalFormat df = new DecimalFormat("#.00");


    //region Convertors
    Location asLocation(World world);

    Block asBlock(World world);

    Vector asVector();

    YawAndPitch direction();

    default BlockPos asBlockPos() {
        return BlockPos.of(this);
    }

    default BlockLoc asBlockLoc() {
        return BlockLoc.of(this);
    }

    default BlockCardinal asBlockCardinal() {
        return BlockCardinal.of(this);
    }

    //endregion


    //region Util
    default double distance(Location location) {
        return distance(BlockPos.of(location));
    }

    default double distanceSquared(Location location) {
        return distanceSquared(BlockPos.of(location));
    }

    default double distance(Block block) {
        return distance(BlockPos.of(block));
    }

    default double distance(Locatable locatable) {
        return Math.sqrt(distanceSquared(locatable));
    }

    default double distanceSquared(Locatable locatable) {
        Vector v1 = this.asVector();
        Vector v2 = locatable.asVector();

        double dx = v1.getX() - v2.getX();
        double dy = v1.getY() - v2.getY();
        double dz = v1.getZ() - v2.getZ();

        return dx * dx + dy * dy + dz * dz;
    }
    //endregion


    //region Math Functionality
    Locatable mult(Locatable other);

    default Locatable mult(Vector vector) {
        return mult(BlockPos.of(vector.getX(), vector.getY(), vector.getZ()));
    }

    default Locatable mult(double scalar) {
        return mult(new Vector(scalar, scalar, scalar));
    }


    Locatable add(Locatable other);

    default Locatable add(Vector vector) {
        return add(BlockPos.of(vector.getX(), vector.getY(), vector.getZ()));
    }

    default Locatable add(double x, double y, double z) {
        return add(new Vector(x, y, z));
    }

    default Location addTo(Location location) {
        Vector vector = asVector();
        YawAndPitch direction = YawAndPitch.from(location).add(direction());
        return direction.applyTo(location.clone().add(vector));
    }

    default Vector addTo(Vector vector) {
        return asVector().add(vector);
    }

    default Locatable translate(double amount, Relative relative) {
        Vector direction = direction().relative(relative).normalize();
        Vector translation = direction.multiply(amount);
        return add(translation);
    }

    //endregion


    //region Rotation Functionality
    Locatable withYawPitch(YawAndPitch rotation);

    default Locatable withYaw(float yaw) {
        return withYawPitch(direction().withYaw(yaw));
    }

    default Locatable withPitch(float pitch) {
        return withYawPitch(direction().withPitch(pitch));
    }

    Locatable rotate(YawAndPitch rotation);

    default Locatable lookTowards(Locatable target) {
        Vector direction = target.asVector().subtract(this.asVector()).normalize();
        float yaw = (float)Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        float pitch = (float)Math.toDegrees(Math.asin(direction.getY()));
        return withYaw(yaw).withPitch(pitch);
    }

    default Locatable lookTowards(Relative relative) {
        Vector direction = direction().relative(relative).normalize();
        float yaw = (float)Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        float pitch = (float)Math.toDegrees(Math.asin(direction.getY()));
        return withYaw(yaw).withPitch(pitch);
    }

    default Locatable rotateYaw(float yaw) {
        return rotate(YawAndPitch.of(yaw, 0));
    }

    default Locatable rotatePitch(float pitch) {
        return rotate(YawAndPitch.of(0, pitch));
    }

    default Locatable rotate(Rotation rotation) {
        return rotate(YawAndPitch.from(rotation));
    }

    //endregion


}
