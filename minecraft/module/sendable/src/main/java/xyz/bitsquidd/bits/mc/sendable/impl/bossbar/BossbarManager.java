/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import net.kyori.adventure.key.Key;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;


public abstract class BossbarManager extends SendableManager<AbstractBossbar> {
    public static final Key BOSSBAR_INDEX = Bits.key("bossbar_index");

    protected BossbarManager() {}

}

