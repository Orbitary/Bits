/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.wrapper;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import xyz.bitsquidd.bits.data.world.Cardinal;

import java.util.Objects;

/**
 * Immutable class representing yaw and pitch angles.
 */
public final class YawAndPitch {
    public final float yaw;
    public final float pitch;

    public static final YawAndPitch ZERO = new YawAndPitch(0f, 0f);

    public static Location remove(Location location) {
        return YawAndPitch.ZERO.applyTo(location);
    }

    // Yaw is normalized between [-180, 180]
    // Pitch is normalized between [-90, 90]
    private YawAndPitch(float yaw, float pitch) {
        this.yaw = ((yaw + 180) % 360) - 180;
        this.pitch = ((pitch + 90) % 180) - 90;
    }

    public static YawAndPitch of(float yaw, float pitch) {
        return new YawAndPitch(yaw, pitch);
    }


    public static YawAndPitch from(Location location) {
        return new YawAndPitch(location.getYaw(), location.getPitch());
    }

    public static YawAndPitch from(Entity entity) {
        return from(entity.getLocation());
    }

    public static YawAndPitch from(BlockFace blockFace) {
        return switch (blockFace) {
            case NORTH -> new YawAndPitch(0, 0);
            case EAST -> new YawAndPitch(90, 0);
            case SOUTH -> new YawAndPitch(180, 0);
            case WEST -> new YawAndPitch(-90, 0);
            case UP -> new YawAndPitch(0, -90);
            case DOWN -> new YawAndPitch(0, 90);
            case NORTH_EAST -> new YawAndPitch(45, 0);
            case SOUTH_EAST -> new YawAndPitch(135, 0);
            case SOUTH_WEST -> new YawAndPitch(-135, 0);
            case NORTH_WEST -> new YawAndPitch(-45, 0);
            case WEST_NORTH_WEST -> new YawAndPitch(-67.5f, 0);
            case NORTH_NORTH_WEST -> new YawAndPitch(-22.5f, 0);
            case NORTH_NORTH_EAST -> new YawAndPitch(22.5f, 0);
            case EAST_NORTH_EAST -> new YawAndPitch(67.5f, 0);
            case EAST_SOUTH_EAST -> new YawAndPitch(112.5f, 0);
            case SOUTH_SOUTH_EAST -> new YawAndPitch(157.5f, 0);
            case SOUTH_SOUTH_WEST -> new YawAndPitch(-157.5f, 0);
            case WEST_SOUTH_WEST -> new YawAndPitch(-112.5f, 0);

            default -> YawAndPitch.ZERO;
        };
    }

    public static YawAndPitch from(Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> new YawAndPitch(0, 0);
            case EAST -> new YawAndPitch(90, 0);
            case SOUTH -> new YawAndPitch(180, 0);
            case WEST -> new YawAndPitch(-90, 0);
            case UP -> new YawAndPitch(0, -90);
            case DOWN -> new YawAndPitch(0, 90);
        };
    }

    public static YawAndPitch from(Quaternionf quaternion) {
        Vector3f euler = new Vector3f();
        quaternion.getEulerAnglesYXZ(euler);
        return new YawAndPitch((float)Math.toDegrees(-euler.y + Math.PI), (float)Math.toDegrees(euler.x));
    }

    public static YawAndPitch from(Rotation rotation) {
        return switch (rotation) {
            case NONE -> YawAndPitch.ZERO;
            case CLOCKWISE_45 -> YawAndPitch.of(45, 0);
            case CLOCKWISE -> YawAndPitch.of(90, 0);
            case CLOCKWISE_135 -> YawAndPitch.of(135, 0);
            case FLIPPED -> YawAndPitch.of(180, 0);
            case FLIPPED_45 -> YawAndPitch.of(270, 0);
            case COUNTER_CLOCKWISE -> YawAndPitch.of(-90, 0);
            case COUNTER_CLOCKWISE_45 -> YawAndPitch.of(-45, 0);
        };
    }


    public YawAndPitch roundYaw(float multiple) {
        float roundedYaw = Math.round(yaw / multiple) * multiple;
        return new YawAndPitch(roundedYaw, pitch);
    }

    public YawAndPitch roundPitch(float multiple) {
        float roundedPitch = Math.round(pitch / multiple) * multiple;
        return new YawAndPitch(yaw, roundedPitch);
    }


    public YawAndPitch add(YawAndPitch yawAndPitch) {
        return new YawAndPitch(this.yaw + yawAndPitch.yaw, this.pitch + yawAndPitch.pitch);
    }

    public YawAndPitch subtract(YawAndPitch yawAndPitch) {
        return new YawAndPitch(this.yaw - yawAndPitch.yaw, this.pitch - yawAndPitch.pitch);
    }

    /**
     * Adds the given yaw and pitch to this one, wrapping around axes if needed.
     */
    public YawAndPitch addWrap(YawAndPitch yawAndPitch) {
        return from(this.toQuaternion().mul(yawAndPitch.toQuaternion())); // We convert to quaternion, add then convert back to avoid gimbal lock
    }

    public YawAndPitch subtractWrap(YawAndPitch yawAndPitch) {
        return from(this.toQuaternion().mul(yawAndPitch.toQuaternion().invert()));
    }


    public YawAndPitch withYaw(float yaw) {
        return new YawAndPitch(yaw, this.pitch);
    }

    public YawAndPitch withPitch(float pitch) {
        return new YawAndPitch(this.yaw, pitch);
    }

    public YawAndPitch withInvertedYaw() {
        return new YawAndPitch(-this.yaw, this.pitch);
    }

    public YawAndPitch withInvertedPitch() {
        return new YawAndPitch(this.yaw, -this.pitch);
    }


    public Quaternionf toQuaternion() {
        Quaternionf quaternion = new Quaternionf();
        quaternion.rotateY((float)Math.toRadians(yaw));
        quaternion.rotateX((float)Math.toRadians(pitch));
        return quaternion;
    }

    public BlockFace toBlockFace() {
        float normalizedYaw = ((yaw % 360) + 360) % 360;

        if (pitch > 45) {
            return BlockFace.DOWN;
        } else if (pitch < -45) {
            return BlockFace.UP;
        } else if (normalizedYaw >= 337.5 || normalizedYaw < 22.5) {
            return BlockFace.NORTH;
        } else if (normalizedYaw >= 22.5 && normalizedYaw < 67.5) {
            return BlockFace.NORTH_EAST;
        } else if (normalizedYaw >= 67.5 && normalizedYaw < 112.5) {
            return BlockFace.EAST;
        } else if (normalizedYaw >= 112.5 && normalizedYaw < 157.5) {
            return BlockFace.SOUTH_EAST;
        } else if (normalizedYaw >= 157.5 && normalizedYaw < 202.5) {
            return BlockFace.SOUTH;
        } else if (normalizedYaw >= 202.5 && normalizedYaw < 247.5) {
            return BlockFace.SOUTH_WEST;
        } else if (normalizedYaw >= 247.5 && normalizedYaw < 292.5) {
            return BlockFace.WEST;
        } else if (normalizedYaw >= 292.5 && normalizedYaw < 337.5) {
            return BlockFace.NORTH_WEST;
        }

        return BlockFace.SELF; // Fallback, should not reach here
    }

    public Cardinal toCardinal() {
        float normalizedYaw = ((yaw % 360) + 360) % 360;

        if (pitch > 45) {
            return Cardinal.DOWN;
        } else if (pitch < -45) {
            return Cardinal.UP;
        } else if (normalizedYaw >= 315 || normalizedYaw < 45) {
            return Cardinal.NORTH;
        } else if (normalizedYaw >= 45 && normalizedYaw < 135) {
            return Cardinal.EAST;
        } else if (normalizedYaw >= 135 && normalizedYaw < 225) {
            return Cardinal.SOUTH;
        } else if (normalizedYaw >= 225) {
            return Cardinal.WEST;
        }

        return Cardinal.NORTH; // Fallback, should not reach here
    }

    public Vector toVector() {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double x = -Math.cos(pitchRad) * Math.sin(yawRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(pitchRad) * Math.cos(yawRad);
        return new Vector(x, y, z);
    }


    public Location applyTo(Location location) {
        location.setRotation(yaw, pitch);
        return location;
    }

    public BlockPos applyTo(BlockPos blockPos) {
        return BlockPos.of(blockPos.x, blockPos.y, blockPos.z, yaw, pitch);
    }

    public Quaternionf addTo(Quaternionf quaternion) {
        quaternion.rotateY((float)Math.toRadians(yaw));
        quaternion.rotateX((float)Math.toRadians(pitch));
        return quaternion;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof YawAndPitch that)) return false;
        return Float.compare(that.yaw, yaw) == 0 && Float.compare(that.pitch, pitch) == 0;
    }

    @Override
    public String toString() {
        return "YawAndPitch:" + yaw + "," + pitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yaw, pitch);
    }

}
