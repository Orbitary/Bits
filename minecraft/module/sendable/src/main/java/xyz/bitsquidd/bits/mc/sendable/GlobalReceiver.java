/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import java.util.UUID;


/**
 * Storage for all globally sent sendables. This is a singleton, and should only be used for sendables sent to all players.
 */
public final class GlobalReceiver implements Receiver {
    public static final GlobalReceiver INSTANCE = new GlobalReceiver();

    private GlobalReceiver() {}

    @Override
    public UUID getUniqueId() {
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    @Override
    public int hashCode() {
        return getUniqueId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GlobalReceiver;
    }

}
