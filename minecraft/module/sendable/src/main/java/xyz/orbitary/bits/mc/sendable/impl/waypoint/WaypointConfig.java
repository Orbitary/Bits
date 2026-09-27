/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable.impl.waypoint;

import xyz.orbitary.bits.mc.sendable.SendableConfig;


public final class WaypointConfig extends SendableConfig {

    private WaypointConfig(Builder builder) {
        super(builder);
    }

    public static final class Builder extends SendableConfig.Builder<Builder> {

        Builder() {}

        @Override
        public WaypointConfig build() {
            return new WaypointConfig(this);
        }

    }

}
