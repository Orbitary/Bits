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
    protected abstract TablistSendableConfig createConfig();

    @Override
    public final TablistSendableConfig config() {
        return (TablistSendableConfig)super.config();
    }


    public abstract Component content(SendableState state);


}
