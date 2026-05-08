/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.SendableOrchestrator;
import xyz.bitsquidd.bits.mc.sendable.collection.KeyedSendableCollection;


public final class BossbarCollection extends KeyedSendableCollection<Integer, AbstractBossbar> {
    BossbarCollection() {}

    @Override
    protected SendableManager<AbstractBossbar, ?> manager() {
        return SendableOrchestrator.get().bossbar();
    }

}
