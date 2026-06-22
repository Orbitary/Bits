/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.Optional;


public abstract class BossbarManager extends SendableManager<AbstractBossbar, BossbarCollection> {

    @Override
    protected BossbarCollection createCollection() {
        return new BossbarCollection();
    }


    //region Operations
    public final <S extends AbstractBossbar> Optional<SendableHandle<S>> add(Receiver receiver, int index, S bossbar) {
        return getOrCreateCollection(receiver).add(index, bossbar);
    }

    public final <S extends AbstractBossbar> void addGlobal(int index, S bossbar) {
        performGlobalAdd(() -> globalSendables.add(index, bossbar), r -> add(r, index, bossbar));
    }
    //endregion

}

