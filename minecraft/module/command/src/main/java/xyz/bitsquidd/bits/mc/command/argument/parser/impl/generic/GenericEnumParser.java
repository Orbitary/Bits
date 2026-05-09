/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.generic;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.abs.AbstractEnumArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A fallback, generically instantiated parser that handles unmapped enum types automatically.
 * <p>
 * <b>Developer Note:</b> Custom enum parsers should override {@link AbstractEnumArgumentParser}
 * if you want more fine-grained control over enum arguments and tab completion.
 * <p>
 * This implementation serves as a basic parser that is fallen back upon when no specific enum parser is available.
 *
 * @param <E> the enum type
 *
 * @since 0.0.10
 */
@ArgumentParser
public final class GenericEnumParser<E extends Enum<E>> extends AbstractArgumentParser<E> {

    private final Class<E> enumClass;

    public GenericEnumParser(Class<E> enumClass) {
        super(TypeSignature.of(enumClass), enumClass.getSimpleName());
        this.enumClass = enumClass;
    }

    @Override
    public E parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        String inputString = singletonInputValidation(inputObjects, String.class);

        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(inputString)) return constant;
        }

        throw ExceptionBuilder.createCommandException("Enum constant not found: " + inputString + " for enum " + enumClass.getSimpleName() + ".");
    }

    @Override
    public Supplier<List<String>> getSuggestions() {
        return () -> Stream.of(enumClass.getEnumConstants()).map(Enum::name).toList();
    }

}
