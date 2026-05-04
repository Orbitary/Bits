/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl;

import xyz.bitsquidd.bits.mc.sendable.SendableConfig;


/**
 * An abstract class representing a sendable entity that can be updated and ticked at a specified rate.
 * <p>
 * Developer Note: Each subclass must implement their own component getting logic.
 */
public abstract sealed class AbstractSendable permits AbstractTitle {
    private final SendableConfig config;

    protected AbstractSendable() {
        this.config = createConfig();
    }

    protected abstract SendableConfig createConfig();

    public SendableConfig config() {
        return config;
    }


}
