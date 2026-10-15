/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command.util;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.mc.command.BitsCommandManager;
import xyz.orbitary.bits.mc.command.annotation.Suggest;
import xyz.orbitary.bits.mc.command.argument.BrigadierArgumentMapping;
import xyz.orbitary.bits.mc.command.argument.parser.ArgumentParser;
import xyz.orbitary.bits.mc.command.exception.CommandBuildException;
import xyz.orbitary.bits.wrapper.GreedyString;
import xyz.orbitary.bits.wrapper.type.TypeSignature;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * Stores argument parsing information and mapping details for a specific command parameter.
 * <p>
 * This class acts as a bridge between Brigadier's built-in argument system and custom internal
 * type parsers registered in the manager.
 * <p>
 * Example internal usage:
 * <pre>{@code
 * CommandParameterInfo paramInfo = new CommandParameterInfo(reflectiveParam);
 * List<ArgumentBuilder<T, ?>> brigArguments = paramInfo.createBrigadierArguments();
 * }</pre>
 *
 * @since 0.0.10
 */
public class CommandParameterInfo {
    private final Parameter parameter;

    private final TypeSignature<?> typeSignature; // TypeSignature of the parameter

    private final ArgumentParser<?, ?> parser;

    private final List<BrigadierArgumentMapping> heldArguments = new ArrayList<>(); // Direct mapping from the custom args to brigadier ArgumentTypes


    public CommandParameterInfo(Parameter parameter) {
        this(parameter, -1);
    }

    public CommandParameterInfo(Parameter parameter, int index) {
        this.parameter = parameter;
        this.typeSignature = TypeSignature.of(parameter.getParameterizedType());

        xyz.orbitary.bits.mc.command.annotation.Parameter parameterAnnotation = parameter.getAnnotation(xyz.orbitary.bits.mc.command.annotation.Parameter.class);
        String name;
        if (parameterAnnotation != null && !parameterAnnotation.value().isEmpty()) {
            name = parameterAnnotation.value();
        } else {
            String parameterName = parameter.getName();
            if (parameterName.contains("arg")) {
                String typeName = typeSignature.toRawType().getSimpleName().toLowerCase();
                name = index >= 0 ? typeName + index : typeName;
            } else {
                name = parameterName;
            }
        }

        this.parser = BitsCommandManager.get().getArgumentRegistry().getParser(typeSignature);
        this.heldArguments.addAll(BitsCommandManager.get().getArgumentRegistry().getArgumentTypeContainer(parser, name));

        if (parameter.isAnnotationPresent(Suggest.class) && heldArguments.size() != 1) {
            throw new CommandBuildException("@Suggest requires exactly one Brigadier argument for parameter " + parameter + " in " + parameter.getDeclaringExecutable());
        }
    }

    /**
     * Constructs the necessary Brigadier argument builders for this parameter mapping.
     *
     * @param <T> the type of the platform's original source object
     *
     * @return a list of Brigadier argument builders
     *
     * @since 0.0.10
     */
    public <T> List<ArgumentBuilder<T, ?>> createBrigadierArguments() {
        List<ArgumentBuilder<T, ?>> brigadierArguments = new ArrayList<>();

        boolean useArgSuggestions = heldArguments.size() > 1; // We use the arg suggestions only if there are multiple held arguments
        Suggest suggest = parameter.getAnnotation(Suggest.class);

        for (int i = 0; i < heldArguments.size(); i++) {
            BrigadierArgumentMapping arg = heldArguments.get(i);
            RequiredArgumentBuilder<T, ?> argumentBuilder = arg.toBrigadierArgument(BitsCommandManager.get());

            if (suggest != null) {
                List<String> suggestions = List.of(suggest.value());
                argumentBuilder.suggests((ctx, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                    for (String suggestion : suggestions) {
                        if (suggestion.toLowerCase(Locale.ROOT).startsWith(remaining)) {
                            builder.suggest(suggestion);
                        }
                    }
                    return builder.buildFuture();
                });
            } else if (useArgSuggestions) {
                TypeSignature<?> inputType = parser.getInputTypes().get(i).typeSignature();
                ArgumentParser<?, ?> inputParser = BitsCommandManager.get().getArgumentRegistry().getParser(inputType);

                // Use the input-specific parser's suggestions
                if (hasSuggestions(inputParser)) argumentBuilder.suggests(inputParser.createSuggestionProvider());
            } else {
                // Use the main parser's suggestions
                if (hasSuggestions(parser)) argumentBuilder.suggests(parser.createSuggestionProvider());
            }

            brigadierArguments.add(argumentBuilder);
        }
        return brigadierArguments;
    }

    /**
     * Reads this parameter's raw Brigadier arguments from the context and parses them into the parameter's type.
     *
     * @param ctx     the Brigadier context holding the raw arguments
     * @param bitsCtx the command context passed to the parser
     *
     * @return the parsed value
     *
     * @throws CommandSyntaxException if the parser rejects the input
     * @since 0.1.0
     */
    public @Nullable Object parse(CommandContext<?> ctx, BitsCommandContext<?> bitsCtx) throws CommandSyntaxException {
        // Collect primitive objects for the parameter
        List<@Nullable Object> primitiveObjects = new ArrayList<>();
        for (int i = 0; i < parser.getInputTypes().size(); i++) {
            BrigadierArgumentMapping holder = heldArguments.get(i);

            Object primitiveValue;
            try {
                Class<?> checkType = holder.typeSignature().toRawType();
                // GreedyString is a semantic marker only; Brigadier's greedyString() argument type
                // physically stores and returns a plain String, so fetch it as one.
                if (checkType == GreedyString.class) checkType = String.class;

                primitiveValue = ctx.getArgument(holder.argumentName(), checkType);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Failed to get argument: " + this, e);
            }

            primitiveObjects.add(primitiveValue);
        }

        try {
            if (primitiveObjects.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("One or more primitive arguments are null.");

            return BitsCommandManager.get().getArgumentRegistry().parseArguments(parser, primitiveObjects, bitsCtx);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to get argument: " + this, e);
        }
    }

    private boolean hasSuggestions(ArgumentParser<?, ?> parser) {
        return parser.getSuggestions() != null;
    }

    /**
     * Returns the underlying argument parser responsible for converting player input.
     *
     * @return the parser instance
     *
     * @since 0.0.10
     */
    public ArgumentParser<?, ?> getParser() {
        return parser;
    }

    /**
     * Returns the mappings that specify how this parameter breaks down into Brigadier types.
     *
     * @return the list of argument mappings
     *
     * @since 0.0.10
     */
    public List<BrigadierArgumentMapping> getHeldArguments() {
        return heldArguments;
    }

    /**
     * Returns the type signature corresponding to the parsed parameter's generic type.
     *
     * @return the parameter type signature
     *
     * @since 0.0.10
     */
    public TypeSignature<?> getTypeSignature() {
        return typeSignature;
    }

}
