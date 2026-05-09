/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.SendableOrchestrator;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;

import java.util.Optional;


public abstract class TablistManager extends SendableManager<AbstractTablist, TablistCollection> {

    @Override
    protected TablistCollection createCollection() {
        return new TablistCollection();
    }


    //region Operations
    public final <S extends AbstractTablist> Optional<SendableHandle<S>> add(Receiver receiver, TablistPosition position, S tablist) {
        return getOrCreateCollection(receiver).add(position, tablist);
    }

    public final <S extends AbstractTablist> void addGlobal(TablistPosition position, S tablist) {
        globalSendables.add(position, tablist);
        SendableOrchestrator.get().getAllReceivers().forEach(r -> add(r, position, tablist));
    }
    //endregion
}
