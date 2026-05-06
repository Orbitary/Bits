/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation.impl;

import xyz.bitsquidd.bits.mc.animation.AnimationPose;

import java.util.Arrays;

public final class CompositeAnimation extends Animation {
    private final Animation[] animations;

    public CompositeAnimation(Animation... animations) {
        this.animations = animations;
    }

    @Override
    public AnimationPose evaluate(int ticks) {
        AnimationPose result = AnimationPose.identity();
        for (Animation animation : animations) {
            result = AnimationPose.add(result, animation.evaluate(ticks));
        }
        return result;
    }

    @Override
    public boolean isFinished(int ticks) {
        return Arrays.stream(animations).allMatch(a -> a.isFinished(ticks));
    }

}