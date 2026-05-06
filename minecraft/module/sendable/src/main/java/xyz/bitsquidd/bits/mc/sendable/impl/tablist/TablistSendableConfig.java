/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


public final class TablistSendableConfig extends SendableConfig {
    private TablistSendableConfig(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends SendableConfig.Builder<Builder> {

        private Builder() {}


        @Override
        public TablistSendableConfig build() {
            return new TablistSendableConfig(this);
        }

    }

}
