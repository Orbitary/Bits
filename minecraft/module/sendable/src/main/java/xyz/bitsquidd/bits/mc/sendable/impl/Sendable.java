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
@SuppressWarnings("unused")
public abstract class Sendable {
    private final SendableConfig config;

    protected Sendable() {
        this.config = createConfig().build();
    }

    protected abstract SendableConfig.Builder<?> createConfig();

    public SendableConfig config() {
        return config;
    }


    public void onAdd(SendableState state) {
        // Default implementation does nothing, override to add add behavior.
    }

    public void onTick(SendableState state) {
        // Default implementation does nothing, override to add tick behavior.
    }

    public void onExpire(SendableState state) {
        // Default implementation does nothing, override to add expire behavior.
    }


    public boolean needsRender(SendableState state) {
        long tickRate = config.tickRate();
        return tickRate > 0 && state.tick() % tickRate == 0;
    }


    public static final long TICK_RATE_DEFAULT = 20L; // Default tick rate for sendables: Updates once per second (if the server runs at 20TPS).
    public static final long MAX_TICKS = Long.MAX_VALUE / 2; // A very large number of ticks, used to represent "infinite" duration for sendables that do not expire. Should be less than Long.MAX_VALUE to avoid overflow when adding fade in/out durations of titles.

}
