/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.collection.KeyedSendableCollection;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;

import java.util.UUID;


public final class TablistManager extends SendableManager<AbstractTablist> {

    @Override
    protected void render(UUID uuid, SendableCollection<AbstractTablist> collection) {
        // TODO
    }

    @Override
    protected SendableCollection<AbstractTablist> createCollection(Receiver receiver) {
        return new KeyedSendableCollection<TablistPosition, AbstractTablist>(receiver) {};
    }

}
