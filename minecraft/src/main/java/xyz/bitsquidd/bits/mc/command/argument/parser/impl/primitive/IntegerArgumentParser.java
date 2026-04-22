/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;

/**
 * Argument parser for Integer values.
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class IntegerArgumentParser extends PrimitiveArgumentParser<Integer> {

    public IntegerArgumentParser() {
        super(Integer.class, "Integer");
    }

}
