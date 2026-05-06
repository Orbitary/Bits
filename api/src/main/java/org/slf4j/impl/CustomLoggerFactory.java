/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class CustomLoggerFactory implements ILoggerFactory {
    @Override
    public Logger getLogger(String s) {
        return xyz.bitsquidd.bits.log.Logger.get().slf4j();
    }

}