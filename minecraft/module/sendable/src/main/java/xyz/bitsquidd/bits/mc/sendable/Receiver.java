/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import java.util.UUID;


/**
 * A receiver of sendables, such as a player.
 */
public interface Receiver {
    //region General
    UUID uniqueId();
    //endregion

    // TODO utility methods e.g. addTitle, addActionbar() etc.

}
