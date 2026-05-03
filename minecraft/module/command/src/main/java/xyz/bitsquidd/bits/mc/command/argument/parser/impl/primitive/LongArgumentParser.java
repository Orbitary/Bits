/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;

/**
 * Argument parser for Long values.
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class LongArgumentParser extends PrimitiveArgumentParser<Long> {

    public LongArgumentParser() {
        super(Long.class, "Long");
    }

}
