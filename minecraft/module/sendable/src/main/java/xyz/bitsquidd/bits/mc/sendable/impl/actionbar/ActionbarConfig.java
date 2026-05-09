/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.actionbar;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


public final class ActionbarConfig extends SendableConfig {
    public final int keepaliveTicks;

    private ActionbarConfig(Builder builder) {
        super(builder);
        this.keepaliveTicks = builder.keepaliveTicks;
    }


    public static final class Builder extends SendableConfig.Builder<Builder> {
        private int keepaliveTicks = 40;

        Builder() {}

        public Builder keepaliveTicks(int keepaliveTicks) {
            this.keepaliveTicks = keepaliveTicks;
            return self();
        }


        @Override
        public ActionbarConfig build() {
            return new ActionbarConfig(this);
        }

    }

}
