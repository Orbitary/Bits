/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.sidebar;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.SendableOrchestrator;
import xyz.bitsquidd.bits.mc.sendable.collection.ListSendableCollection;


public final class SidebarCollection extends ListSendableCollection<AbstractSidebar> {
    SidebarCollection() {}

    @Override
    protected SendableManager<AbstractSidebar, ?> manager() {
        return SendableOrchestrator.get().sidebar();
    }

}
