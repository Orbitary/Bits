/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation;/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import org.joml.Quaternionf;
import org.joml.Vector3f;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;

/**
 * Represents the pose of an animation at a specific point in time, consisting of translation, rotation, and scale.
 * <p>
 * Each component is optional, allowing for partial poses that can be merged with others.
 * For example, null translation means "don't change translation", and thus will be ignored when merging with other poses.
 *
 * @since 0.0.13
 */
public record AnimationPose(
  Vector3f translation,
  Quaternionf rotation,
  Vector3f scale
) {

    public static AnimationPose identity() {
        return new AnimationPose(new Vector3f(), new Quaternionf(), new Vector3f(1));
    }

    public static AnimationPose add(AnimationPose a, AnimationPose b) {
        return new AnimationPose(
          new Vector3f(a.translation).add(b.translation), // Translation combines additively
          new Quaternionf(a.rotation).mul(b.rotation),    // Rotation combines multiplicatively
          new Vector3f(a.scale).mul(b.scale)              // Scale combines multiplicatively
        );
    }

    public static AnimationPose multiply(AnimationPose pose, float scalar) {
        return new AnimationPose(
          new Vector3f(pose.translation).mul(scalar),                      // Scale translation linearly
          new Quaternionf(pose.rotation).slerp(new Quaternionf(), scalar), // Slerp towards identity rotation
          new Vector3f(1).lerp(pose.scale, scalar)                      // Lerp scale towards 1
        );
    }

    public static AnimationPose lerp(AnimationPose from, AnimationPose to, float progress) {
        return new AnimationPose(
          new Vector3f(from.translation).lerp(to.translation, progress), // Lerp translation linearly
          new Quaternionf(from.rotation).slerp(to.rotation, progress),   // Slerp rotation
          new Vector3f(from.scale).lerp(to.scale, progress)              // Lerp scale linearly
        );
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Buildable<AnimationPose> {
        private Vector3f translation = new Vector3f();
        private Quaternionf rotation = new Quaternionf();
        private Vector3f scale = new Vector3f(1);

        private Builder() {}

        public Builder translation(float x, float y, float z) {
            this.translation = new Vector3f(x, y, z);
            return this;
        }

        public Builder translation(Vector3f v) {
            return translation(v.x, v.y, v.z);
        }

        public Builder translateX(float x) {
            translation.x = x;
            return this;
        }

        public Builder translateY(float y) {
            translation.y = y;
            return this;
        }

        public Builder translateZ(float z) {
            translation.z = z;
            return this;
        }


        public Builder rotation(Quaternionf rotation) {
            this.rotation = new Quaternionf(rotation);
            return this;
        }


        public Builder rotateX(float degrees) {
            rotation.rotateX((float)Math.toRadians(degrees));
            return this;
        }

        public Builder rotateY(float degrees) {
            rotation.rotateY((float)Math.toRadians(degrees));
            return this;
        }

        public Builder rotateZ(float degrees) {
            rotation.rotateZ((float)Math.toRadians(degrees));
            return this;
        }


        public Builder scale(float x, float y, float z) {
            this.scale = new Vector3f(x, y, z);
            return this;
        }

        public Builder scale(Vector3f v) {
            return scale(v.x, v.y, v.z);
        }

        public Builder scale(float uniform) {
            return scale(uniform, uniform, uniform);
        }

        public Builder scaleX(float x) {
            scale.x = x;
            return this;
        }

        public Builder scaleY(float y) {
            scale.y = y;
            return this;
        }

        public Builder scaleZ(float z) {
            scale.z = z;
            return this;
        }


        @Override
        public AnimationPose build() {
            return new AnimationPose(translation, rotation, scale);
        }

    }

}