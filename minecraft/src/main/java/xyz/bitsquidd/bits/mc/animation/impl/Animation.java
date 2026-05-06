/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation.impl;

import xyz.bitsquidd.bits.mc.animation.AnimationPose;

public abstract class Animation {

    public abstract AnimationPose evaluate(int ticks);

    public abstract boolean isFinished(int ticks);


    public final Animation and(Animation... others) {
        Animation[] all = new Animation[others.length + 1];
        all[0] = this;
        System.arraycopy(others, 0, all, 1, others.length);
        return new CompositeAnimation(all);
    }


    public static BasicAnimation.Builder of() {
        return new BasicAnimation.Builder();
    }

    public static Animation constant(AnimationPose pose) {
        return new Animation() {
            @Override
            public AnimationPose evaluate(int ticks) {
                return pose;
            }

            @Override
            public boolean isFinished(int ticks) {
                return false;
            }
        };
    }

}