/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;

/**
 * Argument parser for Double values.
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class DoubleArgumentParser extends PrimitiveArgumentParser<Double> {
    public DoubleArgumentParser() {
        super(Double.class, "Double");
    }

}
