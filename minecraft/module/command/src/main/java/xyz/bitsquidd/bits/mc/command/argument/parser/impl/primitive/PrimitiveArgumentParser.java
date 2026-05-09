/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.InputTypeContainer;
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;

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
public abstract class PrimitiveArgumentParser<O> extends AbstractArgumentParser<O> {

    protected PrimitiveArgumentParser(Class<O> outputClass, String argumentName) {
        super(TypeSignature.of(outputClass), argumentName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final O parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        return (O)singletonInputValidation(inputObjects, getTypeSignature().toRawType());
    }

    @Override
    public final List<InputTypeContainer> getInputTypes() {
        return List.of(new InputTypeContainer(getTypeSignature(), getArgumentName()));
    }

}
