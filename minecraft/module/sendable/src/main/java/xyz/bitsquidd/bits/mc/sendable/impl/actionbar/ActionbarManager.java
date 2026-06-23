/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.actionbar;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.collection.OperationSuite;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;


public abstract class ActionbarManager extends SendableManager<AbstractActionbar, OperationSuite.Multiple<AbstractActionbar>, SendableCollection.Multiple<AbstractActionbar>> {

    protected ActionbarManager() {
        super(SendableCollection.Multiple::new);
    }

}
