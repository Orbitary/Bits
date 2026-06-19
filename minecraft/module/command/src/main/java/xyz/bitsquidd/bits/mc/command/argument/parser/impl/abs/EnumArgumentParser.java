/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.abs;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.argument.parser.BasicArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;
import java.util.stream.Stream;


/**
 * Base abstract parser providing common parsing and suggestion logic for enum types.
 * <p>
 * Implementations provide the exact enum class, and this base handles parsing string inputs
 * into enum constants whilst automatically generating case-insensitive tab completion suggestions.
 * <p>
 * Example implementation:
 * <pre>{@code
 * public class MyEnumParser extends AbstractEnumArgumentParser<MyEnum> {
 *     public MyEnumParser() {
 *         super(MyEnum.class);
 *     }
 * }
 * }</pre>
 *
 * @param <T> the specific enum type
 *
 * @since 0.0.10
 */
public abstract class EnumArgumentParser<T extends Enum<T>> extends BasicArgumentParser<T> {
    private final Class<T> enumClass;

    /**
     * @param enumClass the class object representing the enum to parse
     *
     * @throws IllegalArgumentException if the provided class is not an enum
     * @since 0.0.10
     */
    public EnumArgumentParser(Class<T> enumClass) {
        super(TypeSignature.of(Enum.class), enumClass.getName());
        this.enumClass = enumClass;
        if (!enumClass.isEnum()) throw new IllegalArgumentException("Provided class " + enumClass.getName() + " is not an enum!");
    }

    @Override
    public T parse(String data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        T enumValue;
        try {
            enumValue = Enum.valueOf(enumClass, data);
        } catch (IllegalArgumentException e) {
            throw ExceptionBuilder.createCommandException(data + " is not a valid " + enumClass.getSimpleName() + ".");
        }

        return enumValue;
    }

    @Override
    public @Nullable <T> SuggestionSupplier<T> getSuggestions() {
        return () -> enumClass.isEnum() ? Stream.of(enumClass.getEnumConstants()).map(Enum::name).toList() : List.of();
    }

}