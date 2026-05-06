/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;


public abstract class BossbarManager extends SendableManager<AbstractBossbar, BossbarSendableCollection> {

    @Override
    protected BossbarSendableCollection createCollection(Receiver receiver) {
        return new BossbarSendableCollection(receiver);
    }

}

