/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation.impl;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.mc.animation.AnimationLoopMode;
import xyz.bitsquidd.bits.mc.animation.AnimationPose;
import xyz.bitsquidd.bits.mc.animation.Keyframe;
import xyz.bitsquidd.bits.util.math.easing.Easing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BasicAnimation extends Animation {
    private final ImmutableList<Keyframe> keyframes;
    private final int durationTicks;
    private final int framerate;
    private final AnimationLoopMode animationLoopMode;
    private final int loopCount;
    private final int delayTicks;

    private BasicAnimation(Builder builder) {
        this.keyframes = ImmutableList.copyOf(builder.keyframes.stream()
          .sorted(Comparator.comparingDouble(Keyframe::progress))
          .toList());
        this.durationTicks = builder.durationTicks;
        this.framerate = builder.framerate;
        this.animationLoopMode = builder.loopMode;
        this.loopCount = builder.loopCount == null ? 1 : builder.loopCount;
        this.delayTicks = builder.delayTicks;
    }

    @Override
    public AnimationPose evaluate(int ticks) {
        if (isFinished(ticks)) return AnimationPose.identity();
        if (keyframes.isEmpty()) return AnimationPose.identity();
        if (ticks < delayTicks) return AnimationPose.identity();
        if (ticks % framerate != 0) return AnimationPose.identity();
        if (keyframes.size() == 1) return keyframes.getFirst().pose();

        float progress = animationLoopMode.transform((float)(ticks - delayTicks) / (float)durationTicks);

        AnimationPose result = AnimationPose.identity();
        for (int i = 0; i < keyframes.size() - 1; i++) {
            Keyframe kf = keyframes.get(i);
            Keyframe next = keyframes.get(i + 1);
            if (progress < kf.progress()) break;

            float intervalLength = next.progress() - kf.progress();
            float localProgress = Math.min((progress - kf.progress()) / intervalLength, 1f);
            float easedProgress = kf.easing().progress(localProgress);

            AnimationPose delta = AnimationPose.lerp(AnimationPose.identity(), kf.pose(), easedProgress);
            result = AnimationPose.add(result, delta);
        }
        return result;
    }

    @Override
    public boolean isFinished(int ticks) {
        if (loopCount < 0) return false; // infinite
        return ticks >= delayTicks + (durationTicks * loopCount);
    }

    public static class Builder implements Buildable<BasicAnimation> {
        private final List<Keyframe> keyframes = new ArrayList<>();
        private int durationTicks = 20;
        private int framerate = 1;
        private AnimationLoopMode loopMode = AnimationLoopMode.STRAIGHT;
        private @Nullable Integer loopCount = null;
        private int delayTicks = 0;

        public Builder duration(int ticks) {
            this.durationTicks = ticks;
            return this;
        }

        public Builder framerate(int ticks) {
            this.framerate = ticks;
            return this;
        }

        public Builder loop(AnimationLoopMode mode) {
            this.loopMode = mode;
            if (loopCount == null) this.loopCount = -1;
            return this;
        }

        public Builder loopCount(int count) {
            this.loopCount = count;
            return this;
        }

        public Builder delay(int ticks) {
            this.delayTicks = ticks;
            return this;
        }

        public Builder keyframe(Keyframe keyframe) {
            this.keyframes.add(keyframe);
            return this;
        }

        public Builder keyframe(float progress, AnimationPose pose) {
            return keyframe(Keyframe.of(progress, pose));
        }

        public Builder keyframe(float progress, AnimationPose pose, Easing easing) {
            return keyframe(Keyframe.of(progress, pose, easing));
        }

        @Override
        public BasicAnimation build() {
            if (keyframes.isEmpty()) throw new IllegalStateException("Animation requires at least 1 keyframe");
            return new BasicAnimation(this);
        }

    }

}