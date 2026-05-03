/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;

import java.util.List;
import java.util.function.Supplier;

/**
 * Argument parser for Boolean values.
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class BooleanArgumentParser extends PrimitiveArgumentParser<Boolean> {

    public BooleanArgumentParser() {
        super(Boolean.class, "Boolean");
    }

    @Override
    public Supplier<List<String>> getSuggestions() {
        return () -> List.of("true", "false");
    }

}
