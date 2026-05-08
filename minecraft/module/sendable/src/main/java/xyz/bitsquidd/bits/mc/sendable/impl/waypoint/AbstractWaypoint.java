/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.waypoint;

import net.kyori.adventure.key.Key;

import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;

import java.util.Optional;


public sealed abstract class AbstractWaypoint extends Sendable permits AbstractLocationalWaypoint, AbstractTransmittingWaypoint {

    public abstract Key getAssetKey();

    public Optional<Integer> getColor() {
        return Optional.empty();
    }

}
