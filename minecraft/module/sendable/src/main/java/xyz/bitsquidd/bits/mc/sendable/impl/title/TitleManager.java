/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.Optional;


public abstract class TitleManager extends SendableManager<AbstractTitle, TitleCollection> {

    @Override
    protected TitleCollection createCollection() {
        return new TitleCollection();
    }


    //region Operations
    public final <S extends AbstractTitle> Optional<SendableHandle<S>> add(Receiver receiver, S title) {
        return getOrCreateCollection(receiver).add(title);
    }

    public final <S extends AbstractTitle> void addGlobal(S title) {
        performGlobalAdd(() -> globalSendables.add(title), r -> add(r, title));
    }
    //endregion
}
