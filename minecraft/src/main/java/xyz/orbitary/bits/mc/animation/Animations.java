/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.animation;

import xyz.orbitary.bits.util.math.easing.Easings;


public final class Animations {
    private Animations() {}


    public static Animation empty() {
        return Animation.constant(AnimationKeyframe.Empty.INSTANCE);
    }


    //region Floating
    public static Animation floating() {
        return floating(40, 0.5f);
    }

    public static Animation floating(int duration, float height) {
        return Animation.basic(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationKeyframe.Translation.y(0f), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationKeyframe.Translation.y(height), Easings.IN_OUT_SIN)
          .build();
    }
    //endregion


    public static Animation spin(int duration) {
        return Animation.spin(360f / duration);
    }

    public static Animation pulse(int duration, float minScale, float maxScale) {
        return Animation.basic(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationKeyframe.Scale.of(minScale), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationKeyframe.Scale.of(maxScale), Easings.IN_OUT_SIN)
          .build();
    }


    public static Animation swayX(int duration, float angle) {
        return Animation.basic(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationKeyframe.Rotation.x(-angle), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationKeyframe.Rotation.x(angle), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation swayZ(int duration, float angle) {
        return Animation.basic(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationKeyframe.Rotation.z(-angle), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationKeyframe.Rotation.z(angle), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation wiggleX(int duration, float amplitude) {
        return Animation.basic(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationKeyframe.Translation.x(-amplitude), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationKeyframe.Translation.x(amplitude), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation wiggleZ(int duration, float amplitude) {
        return Animation.basic(duration)
          .loop(AnimationLoopMode.PING_PONG)
          .keyframe(0.00f, AnimationKeyframe.Translation.z(-amplitude), Easings.IN_OUT_SIN)
          .keyframe(1.00f, AnimationKeyframe.Translation.z(amplitude), Easings.IN_OUT_SIN)
          .build();
    }

    public static Animation zoomIn(int duration, float startScale, float endScale) {
        return Animation.basic(duration)
          .loops(1)
          .keyframe(0.00f, AnimationKeyframe.Scale.of(startScale), Easings.OUT_BACK)
          .keyframe(1.00f, AnimationKeyframe.Scale.of(endScale), Easings.OUT_BACK)
          .build();
    }

    public static Animation zoomIn(int duration, float startScale) {
        return zoomIn(duration, startScale, 1f);
    }

    public static Animation zoomIn(int duration) {
        return zoomIn(duration, 0f);
    }


    public static Animation zoomOut(int duration, float startScale, float endScale) {
        return Animation.basic(duration)
          .loops(1)
          .keyframe(0.00f, AnimationKeyframe.Scale.of(startScale), Easings.IN_BACK)
          .keyframe(1.00f, AnimationKeyframe.Scale.of(endScale), Easings.IN_BACK)
          .build();
    }

    public static Animation zoomOut(int duration, float startScale) {
        return zoomOut(duration, startScale, 0f);
    }

    public static Animation zoomOut(int duration) {
        return zoomOut(duration, 0f);
    }


    public static Animation moveUp(int duration, float distance) {
        return Animation.basic(duration)
          .loops(1)
          .keyframe(0.00f, AnimationKeyframe.Translation.y(-distance), Easings.OUT_BACK)
          .keyframe(1.00f, AnimationKeyframe.Translation.empty(), Easings.OUT_BACK)
          .build();
    }

    public static Animation moveUp(int duration) {
        return moveUp(duration, 1.0f);
    }

    public static Animation moveDown(int duration, float distance) {
        return Animation.basic(duration)
          .loops(1)
          .keyframe(0.00f, AnimationKeyframe.Translation.empty(), Easings.IN_BACK)
          .keyframe(1.00f, AnimationKeyframe.Translation.y(-distance), Easings.IN_BACK)
          .build();
    }

    public static Animation moveDown(int duration) {
        return moveDown(duration, 1.0f);
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
