/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package bitsquidd.bits.mc.command.util;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import xyz.bitsquidd.bits.config.MinecraftBitsConfig;
import xyz.bitsquidd.bits.mc.command.BitsCommandManager;
import xyz.bitsquidd.bits.mc.command.argument.BrigadierArgumentMapping;
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

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

    private final AbstractArgumentParser<?> parser;

    private final List<BrigadierArgumentMapping> heldArguments = new ArrayList<>(); // Direct mapping from the custom args to brigadier ArgumentTypes


    public CommandParameterInfo(Parameter parameter) {
        this.parameter = parameter;
        this.typeSignature = TypeSignature.of(parameter.getParameterizedType());

        xyz.bitsquidd.bits.mc.command.annotation.Parameter parameterAnnotation = parameter.getAnnotation(xyz.bitsquidd.bits.mc.command.annotation.Parameter.class);
        String name;
        if (parameterAnnotation != null && !parameterAnnotation.value().isEmpty()) {
            name = parameterAnnotation.value();
        } else {
            String parameterName = parameter.getName();
            name = parameterName.contains("arg") ? typeSignature.toRawType().getSimpleName().toLowerCase() : parameterName;
        }

        this.parser = MinecraftBitsConfig.get().getCommandManager().getArgumentRegistry().getParser(typeSignature);
        this.heldArguments.addAll(MinecraftBitsConfig.get().getCommandManager().getArgumentRegistry().getArgumentTypeContainer(parser, name));
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
    @SuppressWarnings("unchecked")
    public <T> List<ArgumentBuilder<T, ?>> createBrigadierArguments() {
        BitsCommandManager<T> commandManager = (BitsCommandManager<T>)MinecraftBitsConfig.get().getCommandManager();
        List<ArgumentBuilder<T, ?>> brigadierArguments = new ArrayList<>();

        boolean useArgSuggestions = heldArguments.size() > 1; // We use the arg suggestions only if there are multiple held arguments

        for (int i = 0; i < heldArguments.size(); i++) {
            BrigadierArgumentMapping arg = heldArguments.get(i);
            RequiredArgumentBuilder<T, ?> argumentBuilder = arg.toBrigadierArgument(commandManager);

            if (useArgSuggestions) {
                TypeSignature<?> inputType = parser.getInputTypes().get(i).typeSignature();
                AbstractArgumentParser<?> inputParser = commandManager.getArgumentRegistry().getParser(inputType);

                // Use the input-specific parser's suggestions
                if (hasSuggestions(inputParser)) argumentBuilder.suggests(inputParser.getSuggestionProvider());
            } else {
                // Use the main parser's suggestions
                if (hasSuggestions(parser)) argumentBuilder.suggests(parser.getSuggestionProvider());
            }

            brigadierArguments.add(argumentBuilder);
        }
        return brigadierArguments;
    }

    private boolean hasSuggestions(AbstractArgumentParser<?> parser) {
        return parser.getSuggestions() != null;
    }

    /**
     * Returns the underlying argument parser responsible for converting player input.
     *
     * @return the parser instance
     *
     * @since 0.0.10
     */
    public AbstractArgumentParser<?> getParser() {
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
