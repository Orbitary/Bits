/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.waypoint;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.SendableOrchestrator;
import xyz.bitsquidd.bits.mc.sendable.collection.ListSendableCollection;


public final class WaypointCollection extends ListSendableCollection<AbstractWaypoint> {
    WaypointCollection() {}

    @Override
    protected SendableManager<AbstractWaypoint, ?> manager() {
        return SendableOrchestrator.get().waypoint();
    }

}
