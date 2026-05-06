/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.actionbar;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;


public abstract class ActionbarManager extends SendableManager<AbstractActionbar, ActionbarSendableCollection> {

    @Override
    protected ActionbarSendableCollection createCollection(Receiver receiver) {
        return new ActionbarSendableCollection(receiver);
    }

}
