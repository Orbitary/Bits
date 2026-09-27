/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable;

import java.util.Collection;


/**
 * A {@link Receiver} that forwards to a collection of other receivers, rather than representing a single connection itself.
 * <p>
 * Manager callbacks that need a real per-connection receiver (e.g. to dispatch a packet) are fanned out across
 * {@link #getChildren()} instead of being invoked with the relay directly.
 *
 * @since 0.0.20
 */
public interface RelayReceiver extends Receiver {

    /**
     * The receivers this relay currently forwards to.
     *
     * @since 0.0.20
     */
    Collection<? extends Receiver> getChildren();

}
