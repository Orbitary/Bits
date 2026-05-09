/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.actionbar;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;


public abstract class AbstractActionbar extends Sendable {


    @Override
    protected ActionbarConfig.Builder createConfig() {
        return new ActionbarConfig.Builder();
    }

    @Override
    public final ActionbarConfig config() {
        return (ActionbarConfig)super.config();
    }


    public abstract Component content(SendableState state);


    @Override
    public final boolean needsRender(SendableState state) {
        return super.needsRender(state) || state.tick() % config().keepaliveTicks == 0;
    }

}
