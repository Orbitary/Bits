/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl.abs;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;
import java.util.function.Supplier;
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
public abstract class AbstractEnumArgumentParser<T extends Enum<T>> extends AbstractArgumentParser<T> {
    private final Class<T> enumClass;

    /**
     * @param enumClass the class object representing the enum to parse
     *
     * @throws IllegalArgumentException if the provided class is not an enum
     * @since 0.0.10
     */
    public AbstractEnumArgumentParser(Class<T> enumClass) {
        super(TypeSignature.of(Enum.class), enumClass.getName());
        this.enumClass = enumClass;
        if (!enumClass.isEnum()) throw new IllegalArgumentException("Provided class " + enumClass.getName() + " is not an enum!");
    }

    @Override
    public T parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        String inputString = singletonInputValidation(inputObjects, String.class);

        T enumValue;
        try {
            enumValue = Enum.valueOf(enumClass, inputString);
        } catch (IllegalArgumentException e) {
            throw ExceptionBuilder.createCommandException(inputString + " is not a valid " + enumClass.getSimpleName() + ".");
        }

        return enumValue;
    }

    @Override
    public @Nullable Supplier<List<String>> getSuggestions() {
        return () -> enumClass.isEnum() ? Stream.of(enumClass.getEnumConstants()).map(Enum::name).toList() : List.of();
    }

}