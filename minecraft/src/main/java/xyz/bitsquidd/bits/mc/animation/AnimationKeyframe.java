/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;


public sealed interface AnimationKeyframe {

    void applyTo(AnimationPose pose, AnimationData data, float proportion);

    default void applyBlended(AnimationPose pose, AnimationData data, AnimationKeyframe other, float proportion) {
        this.applyTo(pose, data, 1f - proportion);
        other.applyTo(pose, data, proportion);
    }


    record Empty() implements AnimationKeyframe {
        public static final Empty INSTANCE = new Empty();

        @Override
        public void applyTo(AnimationPose pose, AnimationData data, float proportion) {
            // No-op
        }

    }


    record Translation(
      Function<AnimationData, Vector3f> translation
    ) implements AnimationKeyframe {

        public static Translation empty() {
            return new Translation(d -> new Vector3f());
        }

        public static Translation of(Vector3f translation) {
            return new Translation(d -> translation);
        }

        public static Translation of(float x, float y, float z) {
            return new Translation(d -> new Vector3f(x, y, z));
        }

        public static Translation x(float x) {
            return new Translation(d -> new Vector3f(x, 0, 0));
        }

        public static Translation y(float y) {
            return new Translation(d -> new Vector3f(0, y, 0));
        }

        public static Translation z(float z) {
            return new Translation(d -> new Vector3f(0, 0, z));
        }


        @Override
        public void applyTo(AnimationPose pose, AnimationData data, float proportion) {
            Vector3f effective = translation.apply(data).lerp(new Vector3f(), 1 - proportion);
            pose.translation().add(effective); // Translation combines additively
        }

    }

    record Rotation(
      Function<AnimationData, Quaternionf> rotation
    ) implements AnimationKeyframe {

        public static Rotation empty() {
            return new Rotation(d -> new Quaternionf());
        }

        public static Rotation of(Quaternionf rotation) {
            return new Rotation(d -> rotation);
        }

        public static Rotation of(float x, float y, float z, float w) {
            return new Rotation(d -> new Quaternionf(x, y, z, w));
        }

        public static Rotation x(float degrees) {
            return new Rotation(d -> new Quaternionf().rotateX((float)Math.toRadians(degrees)));
        }

        public static Rotation y(float degrees) {
            return new Rotation(d -> new Quaternionf().rotateY((float)Math.toRadians(degrees)));
        }

        public static Rotation z(float degrees) {
            return new Rotation(d -> new Quaternionf().rotateZ((float)Math.toRadians(degrees)));
        }


        @Override
        public void applyTo(AnimationPose pose, AnimationData data, float proportion) {
            Quaternionf effective = rotation.apply(data).slerp(new Quaternionf(), 1 - proportion);
            pose.rotation().mul(effective); // Rotation combines multiplicatively
        }

        @Override
        public void applyBlended(AnimationPose pose, AnimationData data, AnimationKeyframe other, float proportion) {
            if (other instanceof Rotation(Function<AnimationData, Quaternionf> otherRotation)) {
                Quaternionf from = this.rotation.apply(data);
                Quaternionf to = otherRotation.apply(data);
                Quaternionf blended = new Quaternionf(from).slerp(to, proportion);
                pose.rotation().mul(blended);
            } else {
                AnimationKeyframe.super.applyBlended(pose, data, other, proportion);
            }
        }

    }

    record Scale(
      Function<AnimationData, Vector3f> scale
    ) implements AnimationKeyframe {

        public static Scale empty() {
            return new Scale(d -> new Vector3f(1));
        }

        public static Scale of(Vector3f scale) {
            return new Scale(d -> scale);
        }

        public static Scale of(float x, float y, float z) {
            return new Scale(d -> new Vector3f(x, y, z));
        }

        public static Scale of(float s) {
            return new Scale(d -> new Vector3f(s, s, s));
        }

        public static Scale x(float x) {
            return new Scale(d -> new Vector3f(x, 1, 1));
        }

        public static Scale y(float y) {
            return new Scale(d -> new Vector3f(1, y, 1));
        }

        public static Scale z(float z) {
            return new Scale(d -> new Vector3f(1, 1, z));
        }


        @Override
        public void applyTo(AnimationPose pose, AnimationData data, float proportion) {
            Vector3f effective = scale.apply(data).lerp(new Vector3f(1), 1 - proportion);
            pose.scale().mul(effective); // Scale combines multiplicatively
        }

        @Override
        public void applyBlended(AnimationPose pose, AnimationData data, AnimationKeyframe other, float proportion) {
            if (other instanceof Scale(Function<AnimationData, Vector3f> otherScale)) {
                Vector3f from = this.scale.apply(data);
                Vector3f to = otherScale.apply(data);
                Vector3f blended = new Vector3f(from).lerp(to, proportion);
                pose.scale().mul(blended);
            } else {
                AnimationKeyframe.super.applyBlended(pose, data, other, proportion);
            }
        }

    }


}
