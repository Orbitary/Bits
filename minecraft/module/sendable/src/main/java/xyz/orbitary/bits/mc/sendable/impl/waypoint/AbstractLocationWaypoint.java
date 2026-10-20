/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable.impl.waypoint;

import xyz.orbitary.bits.mc.sendable.impl.SendableState;
import xyz.orbitary.bits.mc.world.BlockPos;


public non-sealed abstract class AbstractLocationWaypoint extends AbstractWaypoint {

    public abstract BlockPos getPosition(SendableState state);


}
