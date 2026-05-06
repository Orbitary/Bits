/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation;

import xyz.bitsquidd.bits.util.math.easing.Easing;

public record Keyframe(
  float progress,
  AnimationPose pose,
  Easing easing
) {

    public static Keyframe of(float progress, AnimationPose pose, Easing easing) {
        return new Keyframe(progress, pose, easing);
    }

    public static Keyframe of(float progress, AnimationPose pose) {
        return new Keyframe(progress, pose, new Easing.Linear());
    }

}