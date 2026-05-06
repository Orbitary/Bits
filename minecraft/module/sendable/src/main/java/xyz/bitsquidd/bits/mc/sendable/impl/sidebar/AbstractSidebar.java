/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.sidebar;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;

import java.util.List;


public abstract class AbstractSidebar extends Sendable {


    @Override
    protected abstract SidebarSendableConfig createConfig();

    @Override
    public final SidebarSendableConfig config() {
        return (SidebarSendableConfig)super.config();
    }


    public abstract List<Component> content(SendableState state);


}
