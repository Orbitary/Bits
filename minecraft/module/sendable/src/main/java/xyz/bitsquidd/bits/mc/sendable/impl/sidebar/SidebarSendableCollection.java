/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.sidebar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.collection.ListSendableCollection;


public final class SidebarSendableCollection extends ListSendableCollection<AbstractSidebar> {
    SidebarSendableCollection(Receiver receiver) {
        super(receiver);
    }

}
