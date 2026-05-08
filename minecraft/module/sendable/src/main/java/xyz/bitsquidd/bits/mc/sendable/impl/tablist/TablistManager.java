/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import xyz.bitsquidd.bits.mc.sendable.SendableManager;


public abstract class TablistManager extends SendableManager<AbstractTablist, TablistCollection> {

    @Override
    protected TablistCollection createCollection() {
        return new TablistCollection();
    }

}
