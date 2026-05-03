/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;

/**
 * A fallback argument parser that explicitly consumes and returns {@code null} for {@link Void} types.
 * <p>
 * Usually dispatched when no proper parser exists for an unregistered command parameter type.
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class VoidArgumentParser extends AbstractArgumentParser<Void> {

    public VoidArgumentParser() {
        super(TypeSignature.of(Void.class), "Void");
    }

    @Override
    public Void parse(List<Object> inputObjects, BitsCommandContext<?> ctx) {
        return null;
    }

}
