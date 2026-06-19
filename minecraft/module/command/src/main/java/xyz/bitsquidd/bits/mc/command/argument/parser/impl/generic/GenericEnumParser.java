/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.generic;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import xyz.bitsquidd.bits.mc.command.argument.parser.BasicArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.abs.EnumArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.util.wrapper.Enums;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.stream.Stream;


/**
 * A fallback, generically instantiated parser that handles unmapped enum types automatically.
 * <p>
 * <b>Developer Note:</b> Custom enum parsers should override {@link EnumArgumentParser}
 * if you want more fine-grained control over enum arguments and tab completion.
 * <p>
 * This implementation serves as a basic parser that is fallen back upon when no specific enum parser is available.
 *
 * @param <E> the enum type
 *
 * @since 0.0.10
 */

public final class GenericEnumParser<E extends Enum<E>> extends BasicArgumentParser<E> {
    private final Class<E> enumClass;

    public GenericEnumParser(Class<E> enumClass) {
        super(TypeSignature.of(enumClass), enumClass.getSimpleName());
        this.enumClass = enumClass;
    }

    @Override
    public E parse(String data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        return Enums.getFromIdentifier(enumClass, data)
          .orElseThrow(() -> ExceptionBuilder.createCommandException("Enum constant not found: " + data + " for enum " + enumClass.getSimpleName() + "."));
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        return () -> Stream.of(enumClass.getEnumConstants()).map(Enum::name).toList();
    }

}
