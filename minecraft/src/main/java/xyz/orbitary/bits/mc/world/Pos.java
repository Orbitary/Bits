/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.With;

import xyz.orbitary.bits.data.world.Relative;

import java.util.Objects;


@With
public record Pos(
  @JsonProperty(required = true) Vec3 vec,
  @JsonProperty(required = true) Rotation rot
) {
    public Pos {
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
    public static Pos of(Vec3 pos) {
        return new Pos(pos, Rotation.ZERO);
    }


    //region With
    public Pos withYaw(float yaw) {
        return withRot(rot.withYaw(yaw));
    }

    public Pos withPitch(float pitch) {
        return withRot(rot.withPitch(pitch));
    }
    //endregion


    //region Translation
    public Pos translate(Vec3 offset) {
        return withVec(vec.add(offset));
    }

    public Pos translate(double dx, double dy, double dz) {
        return withVec(vec.add(dx, dy, dz));
    }

    public Pos translate(double amount, Relative relative) {
        return translate(rot.relative(relative).scale(amount));
    }
    //endregion


    //region Rotation
    public Pos lookTowards(Vec3 target) {
        return withRot(Rotation.lookingAt(vec, target));
    }

    public Vec3 forward() {
        return rot.forward();
    }
    //endregion


    //region Distance
    public double distanceSquared(Pos other) {
        return vec.distanceSquared(other.vec);
    }

    public double distance(Pos other) {
        return vec.distance(other.vec);
    }
    //endregion


    //region Conversions
    public BlockPos toBlockPos() {
        return vec.toBlockPos();
    }
    //endregion

}
