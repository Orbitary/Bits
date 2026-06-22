/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.waypoint;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.Optional;


public abstract class WaypointManager extends SendableManager<AbstractWaypoint, WaypointCollection> {

    @Override
    protected WaypointCollection createCollection() {
        return new WaypointCollection();
    }


    //region Operations
    public final <S extends AbstractWaypoint> Optional<SendableHandle<S>> add(Receiver receiver, S waypoint) {
        return getOrCreateCollection(receiver).add(waypoint);
    }

    public final <S extends AbstractWaypoint> void addGlobal(S waypoint) {
        performGlobalAdd(() -> globalSendables.add(waypoint), r -> add(r, waypoint));
    }
    //endregion
}
