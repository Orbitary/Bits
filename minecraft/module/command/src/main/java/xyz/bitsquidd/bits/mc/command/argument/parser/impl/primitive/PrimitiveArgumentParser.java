/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;


/**
 * A foundation parser designed specifically for types that correspond to underlying Brigadier primitives.
 * <p>
 * Subclasses generally define parsing logic that trivially translates an object validated by Brigadier
 * back into the identical Java primitive or string.
 *
 * @param <O> the target primitive type being parsed
 *
 * @since 0.0.10
 */
public abstract class PrimitiveArgumentParser<O> extends ArgumentParser<O, O> {

    protected PrimitiveArgumentParser(Class<O> outputClass, String argumentName) {
        super(TypeSignature.of(outputClass), argumentName, outputClass);
    }

    @Override
    public final O parse(O data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        return data;
    }

}
