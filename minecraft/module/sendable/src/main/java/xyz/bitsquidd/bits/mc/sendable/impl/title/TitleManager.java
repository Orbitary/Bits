/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.collection.ListSendableCollection;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;

import java.util.UUID;


public final class TitleManager extends SendableManager<AbstractTitle> {

    @Override
    protected void render(UUID uuid, SendableCollection<AbstractTitle> collection) {
        // TODO
    }

    @Override
    protected SendableCollection<AbstractTitle> createCollection(Receiver receiver) {
        return new ListSendableCollection<AbstractTitle>(receiver) {};
    }

}
