/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


public final class TitleConfig extends SendableConfig {
    public final int fadeInEndTick;
    public final int fadeInDuration;
    public final int fadeOutStartTick;
    public final int fadeOutDuration;
    // Note: The title display time is defined by maxTicks.

    private TitleConfig(Builder builder) {
        super(builder);
        this.fadeInDuration = builder.fadeIn;
        this.fadeOutDuration = builder.fadeOut;
        this.fadeInEndTick = fadeInDuration;
        this.fadeOutStartTick = fadeInEndTick + maxTicks;
    }


    @Override
    public int maxTicks() {
        return super.maxTicks() + fadeInDuration + fadeOutDuration;
    }


    public static final class Builder extends SendableConfig.Builder<Builder> {
        private int fadeIn = 0;
        private int fadeOut = 0;


        Builder() {}

        public Builder fadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return self();
        }

        public Builder fadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return self();
        }


        @Override
        public TitleConfig build() {
            return new TitleConfig(this);
        }

    }

}
