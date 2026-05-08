/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.waypoint;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


public final class WaypointSendableConfig extends SendableConfig {

    private WaypointSendableConfig(Builder builder) {
        super(builder);
    }

    public static final class Builder extends SendableConfig.Builder<Builder> {

        private Builder() {}

        @Override
        public WaypointSendableConfig build() {
            return new WaypointSendableConfig(this);
        }

    }

}
