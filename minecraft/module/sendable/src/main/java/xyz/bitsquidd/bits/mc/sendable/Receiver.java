/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.mc.sendable.impl.AbstractSendable;

import java.util.List;


/**
 * A receiver of sendables, such as a player.
 */
public interface Receiver {
    //region General
    void removeSendables(SendableFilter<AbstractSendable> sendable);

    List<AbstractSendable> getSendables(SendableFilter<AbstractSendable> predicate);

    //endregion

}
