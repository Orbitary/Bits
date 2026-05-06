/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


/**
 * A <b>stateless</b> abstract class representing a sendable entity that can be updated and ticked at a specified rate.
 * <p>
 * Developer Note: Each subclass must implement their own component getting logic.
 */
public abstract class Sendable {
    private final SendableConfig config;

    protected Sendable() {
        this.config = createConfig().build();
    }

    protected abstract SendableConfig.Builder<?> createConfig();

    public SendableConfig config() {
        return config;
    }


    public void onTick(SendableState state) {
        // Default implementation does nothing, override to add tick behavior.
    }

    public void onAdd(SendableState state) {
        // Default implementation does nothing, override to add add behavior.
    }

    public void onExpire(SendableState state) {
        // Default implementation does nothing, override to add expire behavior.
    }


    public boolean needsRender(SendableState state) {
        int tickRate = config.tickRate();
        return tickRate > 0 && state.tick() % tickRate == 0;
    }

    public boolean canTick(SendableState state) {
        return true; // Can be overridden to "pause" for updates.
    }


}
