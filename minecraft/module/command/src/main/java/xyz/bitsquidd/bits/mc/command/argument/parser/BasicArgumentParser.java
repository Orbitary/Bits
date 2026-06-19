/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser;

import xyz.bitsquidd.bits.wrapper.type.TypeSignature;


/**
 * Argument parser designed for types that can be directly parsed from a single string input without any additional validation or transformation.
 *
 * @param <O>
 */
public abstract class BasicArgumentParser<O> extends ArgumentParser<O, String> {
    protected BasicArgumentParser(TypeSignature<?> typeSignature, String argumentName) {
        super(typeSignature, argumentName, String.class);
    }

}
