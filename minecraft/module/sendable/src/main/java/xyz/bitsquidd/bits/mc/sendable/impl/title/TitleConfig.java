/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import org.jetbrains.annotations.Range;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;


public final class TitleConfig extends SendableConfig {
    public final long fadeInEndTick;
    public final long fadeInDuration;
    public final long fadeOutStartTick;
    public final long fadeOutDuration;
    // Note: The title display time is defined by maxTicks.

    private TitleConfig(Builder builder) {
        super(builder);
        this.fadeInDuration = builder.fadeIn;
        this.fadeOutDuration = builder.fadeOut;
        this.fadeInEndTick = fadeInDuration;
        this.fadeOutStartTick = fadeInEndTick + maxTicks;
    }


    @Override
    public long maxTicks() {
        return maxTicks + fadeInDuration + fadeOutDuration;
    }


    public static final class Builder extends SendableConfig.Builder<Builder> {
        private static final long MAX_FADE_DURATION = Sendable.MAX_TICKS / 2; // To prevent overflow when added to a potential MAX_TICKS.

        private long fadeIn = 0;
        private long fadeOut = 0;


        Builder() {}

        public Builder fadeIn(@Range(from = 0, to = MAX_FADE_DURATION) long fadeIn) {
            this.fadeIn = Math.clamp(fadeIn, 0, MAX_FADE_DURATION);
            return self();
        }

        public Builder fadeOut(@Range(from = 0, to = MAX_FADE_DURATION) long fadeOut) {
            this.fadeOut = Math.clamp(fadeOut, 0, MAX_FADE_DURATION);
            return self();
        }


        @Override
        public TitleConfig build() {
            return new TitleConfig(this);
        }

    }

}
