/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.actionbar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.SendableOrchestrator;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.Optional;


public abstract class ActionbarManager extends SendableManager<AbstractActionbar, ActionbarCollection> {

    @Override
    protected ActionbarCollection createCollection() {
        return new ActionbarCollection();
    }


    //region Operations
    public final <S extends AbstractActionbar> Optional<SendableHandle<S>> add(Receiver receiver, S actionbar) {
        return getOrCreateCollection(receiver).add(actionbar);
    }

    public final <S extends AbstractActionbar> void addGlobal(S actionbar) {
        globalSendables.add(actionbar);
        SendableOrchestrator.get().getAllReceivers().forEach(r -> add(r, actionbar));
    }
    //endregion

}
