/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable;

import java.util.Collection;
import java.util.UUID;


/**
 * Storage for all globally sent sendables. This is a singleton, and should only be used for sendables sent to all players.
 */
public final class GlobalReceiver implements RelayReceiver {
    public static final GlobalReceiver INSTANCE = new GlobalReceiver();
    private static final UUID GLOBAL_RECEIVER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private GlobalReceiver() {}

    @Override
    public UUID getUniqueId() {
        return GLOBAL_RECEIVER_UUID;
    }

    @Override
    public Collection<? extends Receiver> getChildren() {
        return SendableOrchestrator.get().getAllReceivers();
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
