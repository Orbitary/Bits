/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import xyz.bitsquidd.bits.mc.command.BrigadierTreeGenerator;
import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.GreedyString;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;


/**
 * Argument parser for {@link GreedyString} values.
 * <p>
 * This parser is special as it is {@link BrigadierTreeGenerator#createCommandExecution hardcoded}  to capture all remaining inputs as a single string.
 *
 * @since 0.0.10
 */

public final class GreedyStringArgumentParser extends ArgumentParser<GreedyString, String> {
    public GreedyStringArgumentParser() {
        super(TypeSignature.of(GreedyString.class), "String...", String.class);
    }

    @Override
    public GreedyString parse(String data, BitsCommandContext<?> ctx) {
        return GreedyString.of(data);
    }

}
