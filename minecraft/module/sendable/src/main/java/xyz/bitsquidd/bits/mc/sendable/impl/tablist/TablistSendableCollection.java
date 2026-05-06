/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.collection.KeyedSendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;


public final class TablistSendableCollection extends KeyedSendableCollection<TablistPosition, AbstractTablist> {
    TablistSendableCollection(Receiver receiver) {
        super(receiver);
    }

}
