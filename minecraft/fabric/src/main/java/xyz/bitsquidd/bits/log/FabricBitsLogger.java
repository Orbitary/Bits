/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log;

import java.io.File;

public class FabricBitsLogger extends BasicLogger {
    private final org.apache.logging.log4j.Logger output;

    public FabricBitsLogger(org.slf4j.Logger slf4j, LogFlags flags) {
        this(slf4j, flags, new File("logs/bits.log"));
    }

    /**
     * @param logFile the file to mirror console output into, relative to the game directory
     *
     * @since 0.0.23
     */
    public FabricBitsLogger(org.slf4j.Logger slf4j, LogFlags flags, File logFile) {
        super(slf4j, flags);
        this.output = Log4jConsole.create(slf4j.getName(), logFile);
    }

    @Override
    protected void writeLine(final String line) {
        output.info(line);
    }

}