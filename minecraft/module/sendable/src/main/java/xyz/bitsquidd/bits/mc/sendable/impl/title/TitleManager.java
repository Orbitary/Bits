/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.collection.OperationSuite;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;


public abstract class TitleManager extends SendableManager<AbstractTitle, OperationSuite.Multiple<AbstractTitle>, SendableCollection.Multiple<AbstractTitle>> {

    protected TitleManager() {
        super(SendableCollection.Multiple::new);
    }

}
