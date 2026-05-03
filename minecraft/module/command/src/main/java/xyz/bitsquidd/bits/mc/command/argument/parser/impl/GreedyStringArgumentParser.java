/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import xyz.bitsquidd.bits.mc.command.BrigadierTreeGenerator;
import xyz.bitsquidd.bits.mc.command.argument.InputTypeContainer;
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.GreedyString;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;

/**
 * Argument parser for {@link GreedyString} values.
 * <p>
 * This parser is special as it is {@link BrigadierTreeGenerator#createCommandExecution hardcoded}  to capture all remaining inputs as a single string.
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class GreedyStringArgumentParser extends AbstractArgumentParser<GreedyString> {
    public GreedyStringArgumentParser() {
        super(TypeSignature.of(GreedyString.class), "String...");
    }

    @Override
    public GreedyString parse(List<Object> inputObjects, BitsCommandContext<?> ctx) {
        return GreedyString.of((String)inputObjects.getFirst()); // Note: can't use singletonInputValidation as GreedyString is special...
    }

    @Override
    public List<InputTypeContainer> getInputTypes() {
        return List.of(new InputTypeContainer(TypeSignature.of(GreedyString.class), getArgumentName()));
    }

}
