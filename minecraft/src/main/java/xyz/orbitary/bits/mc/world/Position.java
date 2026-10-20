/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;

import xyz.orbitary.bits.data.world.Relative;

import java.util.Objects;


public record Position(
  @JsonProperty(required = true) Vec3 pos,
  @JsonProperty(required = true) Rotation rot
) {
    public Position {
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(rot, "rot");
    }


    public double x() {
        return pos.x();
    }

    public double y() {
        return pos.y();
    }

    public double z() {
        return pos.z();
    }

    public float yaw() {
        return rot.yaw();
    }

    public float pitch() {
        return rot.pitch();
    }


    /**
     * @return the position at {@code pos} facing {@link Rotation#ZERO}.
     */
    public static Position of(Vec3 pos) {
        return new Position(pos, Rotation.ZERO);
    }


    //region With
    public Position withPos(Vec3 pos) {
        return new Position(pos, rot);
    }

    public Position withRot(Rotation rot) {
        return new Position(pos, rot);
    }

    public Position withYaw(float yaw) {
        return withRot(rot.withYaw(yaw));
    }

    public Position withPitch(float pitch) {
        return withRot(rot.withPitch(pitch));
    }
    //endregion


    //region Translation
    public Position translate(Vec3 offset) {
        return withPos(pos.add(offset));
    }

    public Position translate(double dx, double dy, double dz) {
        return withPos(pos.add(dx, dy, dz));
    }

    public Position translate(double amount, Relative relative) {
        return translate(rot.relative(relative).scale(amount));
    }
    //endregion


    //region Rotation
    public Position lookTowards(Vec3 target) {
        return withRot(Rotation.lookingAt(pos, target));
    }

    public Vec3 forward() {
        return rot.forward();
    }
    //endregion


    //region Distance
    public double distanceSquared(Position other) {
        return pos.distanceSquared(other.pos);
    }

    public double distance(Position other) {
        return pos.distance(other.pos);
    }
    //endregion


    //region Conversions
    public BlockPos toBlockPos() {
        return pos.toBlockPos();
    }
    //endregion

}
