/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.sidebar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.Optional;


public abstract class SidebarManager extends SendableManager<AbstractSidebar, SidebarCollection> {

    @Override
    protected SidebarCollection createCollection() {
        return new SidebarCollection();
    }


    //region Operations
    public final <S extends AbstractSidebar> Optional<SendableHandle<S>> add(Receiver receiver, S sidebar) {
        return getOrCreateCollection(receiver).add(sidebar);
    }

    public final <S extends AbstractSidebar> void addGlobal(S sidebar) {
        performGlobalAdd(() -> globalSendables.add(sidebar), r -> add(r, sidebar));
    }
    //endregion
}
