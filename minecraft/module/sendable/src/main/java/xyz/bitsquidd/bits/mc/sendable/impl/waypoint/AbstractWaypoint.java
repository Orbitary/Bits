/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.waypoint;

import net.kyori.adventure.key.Key;

import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;

import java.util.Optional;


public sealed abstract class AbstractWaypoint extends Sendable permits AbstractLocationWaypoint, AbstractTransmittingWaypoint {

    @Override
    protected WaypointConfig.Builder createConfig() {
        return new WaypointConfig.Builder();
    }

    public abstract Key getAssetKey(SendableState state);

    public Optional<Integer> getColor(SendableState state) {
        return Optional.of(0xffffff);
    }

}
