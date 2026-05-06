/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.sidebar;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


public final class SidebarSendableConfig extends SendableConfig {

    private SidebarSendableConfig(Builder builder) {
        super(builder);
    }


    public static final class Builder extends SendableConfig.Builder<Builder> {

        Builder() {}


        @Override
        public SidebarSendableConfig build() {
            return new SidebarSendableConfig(this);
        }

    }

}
