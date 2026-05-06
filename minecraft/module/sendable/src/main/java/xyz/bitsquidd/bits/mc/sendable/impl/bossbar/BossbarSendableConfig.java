/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.data.BossBarColor;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.data.BossBarOverlay;
import xyz.bitsquidd.bits.wrapper.Percentage;


public final class BossbarSendableConfig extends SendableConfig {
    public final BossBarOverlay defaultOverlay;
    public final BossBarColor defaultColor;
    public final Percentage defaultProgress;

    private BossbarSendableConfig(Builder builder) {
        super(builder);
        this.defaultOverlay = builder.defaultOverlay;
        this.defaultColor = builder.defaultColor;
        this.defaultProgress = builder.defaultProgress;
    }

    
    public static final class Builder extends SendableConfig.Builder<Builder> {
        private BossBarOverlay defaultOverlay = BossBarOverlay.DEFAULT;
        private BossBarColor defaultColor = BossBarColor.DEFAULT;
        private Percentage defaultProgress = Percentage.ZERO;

        Builder() {}


        public Builder defaultOverlay(BossBarOverlay defaultOverlay) {
            this.defaultOverlay = defaultOverlay;
            return this;
        }

        public Builder defaultColor(BossBarColor defaultColor) {
            this.defaultColor = defaultColor;
            return this;
        }

        public Builder defaultProgress(Percentage defaultProgress) {
            this.defaultProgress = defaultProgress;
            return this;
        }


        @Override
        public BossbarSendableConfig build() {
            return new BossbarSendableConfig(this);
        }

    }

}
