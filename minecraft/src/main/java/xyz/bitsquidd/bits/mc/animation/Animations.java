/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation;

import xyz.bitsquidd.bits.mc.animation.impl.Animation;
import xyz.bitsquidd.bits.util.math.easing.Easings;

public final class Animations {
    private Animations() {}

    public static Animation floating() {
        return floating(40, 0.5f);
    }


    public static Animation xTranslation(float distance) {
        return Animation.constant(AnimationPose.builder().translateX(distance).build());
    }

    public static Animation yTranslation(float height) {
        return Animation.constant(AnimationPose.builder().translateY(height).build());
    }

    public static Animation zTranslation(float distance) {
        return Animation.constant(AnimationPose.builder().translateZ(distance).build());
    }


    public static Animation floating(int duration, float height) {
        return Animation.of()
          .duration(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationPose.builder().translateY(0f).build(), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationPose.builder().translateY(height).build(), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation spin(int duration) {
        return Animation.of()
          .duration(duration)
          .loop(AnimationLoopMode.STRAIGHT)
          .keyframe(0.00f, AnimationPose.builder().rotateY(0f).build(), Easings.IN_OUT_SIN.blend(Easings.LINEAR, 0.5f))
          .keyframe(1.00f, AnimationPose.builder().rotateY(360f).build(), Easings.IN_OUT_SIN.blend(Easings.LINEAR, 0.5f))
          .build();
    }

    public static Animation pulse(int duration, float minScale, float maxScale) {
        return Animation.of()
          .duration(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationPose.builder().scale(minScale).build(), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationPose.builder().scale(maxScale).build(), Easings.IN_OUT_SIN)
          .build();
    }


    public static Animation swayX(int duration, float angle) {
        return Animation.of()
          .duration(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationPose.builder().rotateX(-angle).build(), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationPose.builder().rotateX(angle).build(), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation swayZ(int duration, float angle) {
        return Animation.of()
          .duration(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationPose.builder().rotateZ(-angle).build(), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationPose.builder().rotateZ(angle).build(), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation wiggleX(int duration, float amplitude) {
        return Animation.of()
          .duration(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationPose.builder().translateX(-amplitude).build(), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationPose.builder().translateX(amplitude).build(), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation wiggleZ(int duration, float amplitude) {
        return Animation.of()
          .duration(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationPose.builder().translateZ(-amplitude).build(), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationPose.builder().translateZ(amplitude).build(), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation zoomIn(int duration) {
        return Animation.of()
          .duration(duration)
          .keyframe(0.00f, AnimationPose.builder().scale(0).build(), Easings.OUT_BACK)
          .keyframe(1.00f, AnimationPose.builder().scale(1f).build(), Easings.OUT_BACK)
          .build();
    }

    public static Animation zoomOut(int duration) {
        return Animation.of()
          .duration(duration)
          .keyframe(0.00f, AnimationPose.builder().scale(1f).build(), Easings.IN_BACK)
          .keyframe(1.00f, AnimationPose.builder().scale(0).build(), Easings.IN_BACK)
          .build();
    }


    //region Composite Animations
    public static Animation floatSpin() {
        return floating(40, 0.5f).and(spin(30));
    }

    public static Animation hotAirBalloon() {
        return floating(400, 1.0f)      // slow rise and fall
          .and(wiggleX(80, 0.15f))   // drift X
          .and(wiggleZ(100, 0.09f))  // drift Z - slight different duration to give an offset feel
          .and(swayX(90, 2.0f))         // atmospheric tilt X
          .and(swayZ(110, 1.5f))        // atmospheric tilt Z
          .and(spin(2000));
    }
    //endregion

}
