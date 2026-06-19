/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;

import java.util.List;


/**
 * Argument parser for Boolean values.
 *
 * @since 0.0.10
 */

public final class BooleanArgumentParser extends PrimitiveArgumentParser<Boolean> {

    public BooleanArgumentParser() {
        super(Boolean.class, "Boolean");
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        return ctx -> List.of("true", "false");
    }

}
