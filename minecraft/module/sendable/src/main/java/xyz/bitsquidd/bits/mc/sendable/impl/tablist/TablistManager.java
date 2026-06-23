/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.collection.OperationSuite;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;


public abstract class TablistManager extends SendableManager<AbstractTablist, OperationSuite.Keyed<TablistPosition, AbstractTablist>, SendableCollection.Keyed<TablistPosition, AbstractTablist>> {

    protected TablistManager() {
        super(SendableCollection.Keyed::new);
    }

}
