/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import xyz.orbitary.bits.data.world.Cardinal;
import xyz.orbitary.bits.data.world.Relative;


public record Rotation(
  @JsonProperty(required = true) float yaw,
  @JsonProperty(required = true) float pitch
) {
    public static final Rotation ZERO = new Rotation(0f, 0f);

    // Minecraft convention: yaw 0 faces south (+Z), 90 west, 180 north, -90 east. Pitch -90 looks up.
    // Yaw is normalized to [-180, 180), pitch is clamped to [-90, 90].
    public Rotation {
        if (!Float.isFinite(yaw) || !Float.isFinite(pitch)) throw new IllegalArgumentException("Rotation must be finite: " + yaw + "," + pitch);
        if (yaw < -180f || yaw >= 180f) yaw -= 360f * (float)Math.floor((yaw + 180f) / 360f);
        yaw += 0.0f;
        pitch = Math.clamp(pitch, -90f, 90f) + 0.0f;
    }

    //region Constructors
    public static Rotation of(float yaw, float pitch) {
        return new Rotation(yaw, pitch);
    }

    public static Rotation of(Cardinal cardinal) {
        return switch (cardinal) {
            case SOUTH -> new Rotation(0, 0);
            case WEST -> new Rotation(90, 0);
            case NORTH -> new Rotation(180, 0);
            case EAST -> new Rotation(-90, 0);
            case UP -> new Rotation(0, -90);
            case DOWN -> new Rotation(0, 90);
        };
    }

    public static Rotation of(Quaternionf quaternion) {
        Vector3f euler = new Vector3f();
        quaternion.getEulerAnglesYXZ(euler);
        return new Rotation((float)Math.toDegrees(-euler.y), (float)Math.toDegrees(euler.x));
    }

    public static Rotation facing(Vec3 direction) {
        double length = direction.length();
        if (length == 0) return ZERO;

        return new Rotation(
          (float)Math.toDegrees(Math.atan2(-direction.x(), direction.z())),
          (float)Math.toDegrees(-Math.asin(Math.clamp(direction.y() / length, -1.0, 1.0)))
        );
    }

    public static Rotation lookingAt(Vec3 from, Vec3 to) {
        return facing(to.subtract(from));
    }
    //endregion


    //region Rounding
    public Rotation roundYaw(float multiple) {
        float roundedYaw = Math.round(yaw / multiple) * multiple;
        return new Rotation(roundedYaw, pitch);
    }

    public Rotation roundPitch(float multiple) {
        float roundedPitch = Math.round(pitch / multiple) * multiple;
        return new Rotation(yaw, roundedPitch);
    }
    //endregion


    public Rotation add(Rotation rotation) {
        return new Rotation(this.yaw + rotation.yaw, this.pitch + rotation.pitch);
    }

    public Rotation subtract(Rotation rotation) {
        return new Rotation(this.yaw - rotation.yaw, this.pitch - rotation.pitch);
    }

    public Rotation addWrap(Rotation rotation) {
        // We convert to quaternion, add then convert back to avoid gimbal lock
        return of(this.toQuaternion().mul(rotation.toQuaternion()));
    }

    public Rotation subtractWrap(Rotation rotation) {
        // We convert to quaternion, add then convert back to avoid gimbal lock
        return of(this.toQuaternion().mul(rotation.toQuaternion().invert()));
    }


    public Rotation withYaw(float yaw) {
        return new Rotation(yaw, this.pitch);
    }

    public Rotation withPitch(float pitch) {
        return new Rotation(this.yaw, pitch);
    }

    public Rotation withInvertedYaw() {
        return new Rotation(-this.yaw, this.pitch);
    }

    public Rotation withInvertedPitch() {
        return new Rotation(this.yaw, -this.pitch);
    }


    public Quaternionf toQuaternion() {
        return addTo(new Quaternionf());
    }

    public Cardinal toCardinal() {
        if (pitch > 45) {
            return Cardinal.DOWN;
        } else if (pitch < -45) {
            return Cardinal.UP;
        }

        return Cardinal.fromYaw(yaw);
    }


    //region Turning
    public Rotation inverse() {
        return new Rotation(yaw + 180, -pitch);
    }

    public Rotation turnLeft() {
        return new Rotation(yaw - 90, pitch);
    }

    public Rotation turnRight() {
        return new Rotation(yaw + 90, pitch);
    }

    public Rotation lookUp() {
        return new Rotation(yaw, pitch - 90);
    }

    public Rotation lookDown() {
        return new Rotation(yaw, pitch + 90);
    }
    //endregion


    //region Direction Vectors
    public Vec3 forward() {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double horizontal = Math.cos(pitchRad);
        return new Vec3(-horizontal * Math.sin(yawRad), -Math.sin(pitchRad), horizontal * Math.cos(yawRad));
    }

    public Vec3 backward() {
        return forward().negate();
    }

    public Vec3 left() {
        double yawRad = Math.toRadians(yaw);
        return new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
    }

    public Vec3 right() {
        return left().negate();
    }

    public Vec3 up() {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double sinPitch = Math.sin(pitchRad);
        return new Vec3(-sinPitch * Math.sin(yawRad), Math.cos(pitchRad), sinPitch * Math.cos(yawRad));
    }

    public Vec3 down() {
        return up().negate();
    }

    public Vec3 relative(Relative relative) {
        return switch (relative) {
            case FORWARD -> forward();
            case BACKWARD -> backward();
            case LEFT -> left();
            case RIGHT -> right();
            case UP -> up();
            case DOWN -> down();
        };
    }
    //endregion


    public Quaternionf addTo(Quaternionf quaternion) {
        quaternion.rotateY((float)Math.toRadians(-yaw));
        quaternion.rotateX((float)Math.toRadians(pitch));
        return quaternion;
    }


    //region Java Overrides
    @Override
    public String toString() {
        return "Rotation:" + yaw + "," + pitch;
    }

    //endregion

}