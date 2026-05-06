/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.sidebar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.collection.ListSendableCollection;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;

import java.util.UUID;


public final class SidebarManager extends SendableManager<AbstractSidebar> {

    @Override
    protected void render(UUID uuid, SendableCollection<AbstractSidebar> collection) {
        // TODO
    }

    @Override
    protected SendableCollection<AbstractSidebar> createCollection(Receiver receiver) {
        return new ListSendableCollection<AbstractSidebar>(receiver) {};
    }

}
