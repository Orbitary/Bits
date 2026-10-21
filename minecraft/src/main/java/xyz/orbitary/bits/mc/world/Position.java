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
  @JsonProperty(required = true) Vec3 vec,
  @JsonProperty(required = true) Rotation rot
) {
    public Position {
        Objects.requireNonNull(vec, "vec");
        Objects.requireNonNull(rot, "rot");
    }


    public double x() {
        return vec.x();
    }

    public double y() {
        return vec.y();
    }

    public double z() {
        return vec.z();
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
    public Position withPos(Vec3 vec) {
        return new Position(vec, rot);
    }

    public Position withRot(Rotation rot) {
        return new Position(vec, rot);
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
        return withPos(vec.add(offset));
    }

    public Position translate(double dx, double dy, double dz) {
        return withPos(vec.add(dx, dy, dz));
    }

    public Position translate(double amount, Relative relative) {
        return translate(rot.relative(relative).scale(amount));
    }
    //endregion


    //region Rotation
    public Position lookTowards(Vec3 target) {
        return withRot(Rotation.lookingAt(vec, target));
    }

    public Vec3 forward() {
        return rot.forward();
    }
    //endregion


    //region Distance
    public double distanceSquared(Position other) {
        return vec.distanceSquared(other.vec);
    }

    public double distance(Position other) {
        return vec.distance(other.vec);
    }
    //endregion


    //region Conversions
    public BlockPos toBlockPos() {
        return vec.toBlockPos();
    }
    //endregion

}
