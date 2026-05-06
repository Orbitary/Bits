/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.collection.KeyedSendableCollection;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;

import java.util.UUID;


public final class BossbarManager extends SendableManager<AbstractBossbar> {

    @Override
    protected void render(UUID uuid, SendableCollection<AbstractBossbar> collection) {
        // TODO
    }

    @Override
    protected SendableCollection<AbstractBossbar> createCollection(Receiver receiver) {
        return new KeyedSendableCollection<Integer, AbstractBossbar>(receiver) {};
    }

}

