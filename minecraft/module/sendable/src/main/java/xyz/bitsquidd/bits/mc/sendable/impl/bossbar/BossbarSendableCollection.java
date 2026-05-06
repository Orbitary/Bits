/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.collection.KeyedSendableCollection;


public final class BossbarSendableCollection extends KeyedSendableCollection<Integer, AbstractBossbar> {
    BossbarSendableCollection(Receiver receiver) {
        super(receiver);
    }

}
