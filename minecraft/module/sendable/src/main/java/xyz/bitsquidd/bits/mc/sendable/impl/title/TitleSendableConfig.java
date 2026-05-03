/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


public final class TitleSendableConfig extends SendableConfig {
    public final int fadeIn;
    // Note: The title display time is defined by maxTicks.
    public final int fadeOut;

    public TitleSendableConfig(Builder builder) {
        super(builder);
        this.fadeIn = builder.fadeIn;
        this.fadeOut = builder.fadeOut;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends SendableConfig.Builder<Builder> {
        private int fadeIn = 0;
        private int fadeOut = 0;

        
        private Builder() {}

        public Builder fadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return self();
        }

        public Builder fadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return self();
        }


        @Override
        public SendableConfig build() {
            return new TitleSendableConfig(this);
        }

    }

}
