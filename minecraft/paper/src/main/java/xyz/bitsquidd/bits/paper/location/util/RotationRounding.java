/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.util;

import org.joml.Quaternionf;

import xyz.bitsquidd.bits.paper.location.wrapper.YawAndPitch;

/**
 * Functional interface for rounding a rotation.
 *
 * @since 0.0.13
 */
@FunctionalInterface
public interface RotationRounding {
    Quaternionf apply(Quaternionf rotation);

    default YawAndPitch apply(YawAndPitch rotation) {
        return YawAndPitch.from(apply(rotation.toQuaternion()));
    }

    RotationRounding NONE = rotation -> rotation;

    RotationRounding ZERO = rotation -> {
        rotation.rotationX(0);
        rotation.rotationY(0);
        rotation.rotationZ(0);
        return rotation;
    };

    RotationRounding ZERO_XZ = rotation -> {
        float yaw = 2 * (float)Math.atan2(rotation.y, rotation.w);
        rotation.identity().rotateY(yaw);
        return rotation;
    };

    RotationRounding NEAREST_90 = combined(roundedYaw(90), roundedPitch(90));

    RotationRounding NEAREST_45 = combined(roundedYaw(45), roundedPitch(0));

    RotationRounding NEAREST_225 = combined(roundedYaw(22.5f), roundedPitch(0));

    RotationRounding BLOCKFACE_90 = rotation -> {
        float yaw = 2 * (float)Math.atan2(rotation.y, rotation.w);
        yaw = (yaw % (2 * (float)Math.PI) + 2 * (float)Math.PI) % (2 * (float)Math.PI);
        float snappedYaw = Math.round(yaw / ((float)Math.PI / 2)) * ((float)Math.PI / 2);

        float pitch = (float)Math.asin(2 * (rotation.w * rotation.x - rotation.y * rotation.z));
        float snappedPitch = Math.round(pitch / ((float)Math.PI / 2)) * ((float)Math.PI / 2);

        rotation.identity().rotateY(snappedYaw).rotateX(snappedPitch);
        return rotation;
    };


    //region Util
    static RotationRounding setYaw(float yaw) {
        return rotation -> {
            rotation.rotationY((float)Math.toRadians(yaw));
            return rotation;
        };
    }

    static RotationRounding setPitch(float pitch) {
        return rotation -> {
            rotation.rotationX((float)Math.toRadians(pitch));
            return rotation;
        };
    }

    static RotationRounding roundedYaw(float multiple) {
        return rotation -> YawAndPitch.from(rotation).roundYaw(multiple).toQuaternion();
    }

    static RotationRounding roundedPitch(float multiple) {
        return rotation -> YawAndPitch.from(rotation).roundPitch(multiple).toQuaternion();
    }
    //endregion


    //region Combination
    static RotationRounding combined(RotationRounding... roundings) {
        return rotation -> {
            for (RotationRounding rounding : roundings) {
                rotation = rounding.apply(rotation);
            }
            return rotation;
        };
    }
    //endregion

}
