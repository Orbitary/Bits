/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import net.kyori.adventure.key.Key;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;


public abstract class TablistManager extends SendableManager<AbstractTablist> {
    public static final Key TABLIST_INDEX = Bits.key("tablist_index");

    protected TablistManager() {}

}
