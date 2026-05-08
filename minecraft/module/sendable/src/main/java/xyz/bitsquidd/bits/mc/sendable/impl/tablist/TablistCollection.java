/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.SendableOrchestrator;
import xyz.bitsquidd.bits.mc.sendable.collection.KeyedSendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;


public final class TablistCollection extends KeyedSendableCollection<TablistPosition, AbstractTablist> {
    TablistCollection() {}

    @Override
    protected SendableManager<AbstractTablist, ?> manager() {
        return SendableOrchestrator.get().tablist();
    }

}
