/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.tablist;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;


public abstract class AbstractTablist extends Sendable {


    @Override
    protected TablistConfig.Builder createConfig() {
        return new TablistConfig.Builder();
    }

    @Override
    public final TablistConfig config() {
        return (TablistConfig)super.config();
    }


    public abstract Component content(SendableState state);


}
