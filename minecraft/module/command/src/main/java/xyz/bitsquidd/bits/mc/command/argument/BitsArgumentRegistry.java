/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.GreedyStringArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.VoidArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.generic.GenericEnumParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive.PrimitiveArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.CommandBuildException;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;
import xyz.bitsquidd.bits.util.reflection.ScannerFlags;
import xyz.bitsquidd.bits.wrapper.GreedyString;
import xyz.bitsquidd.bits.wrapper.collection.AddableSet;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Registry responsible for mapping generic Java types to command argument parsers.
 * <p>
 * This class initialises default parsers for primitives and common classes, and allows implementations
 * to register custom parsers for specific types. It handles the resolution of nested generic types
 * and recursively building Brigadier argument trees.
 * <p>
 * Example internal usage:
 * <pre>{@code
 * BitsArgumentRegistry<?> registry = manager.getArgumentRegistry();
 * AbstractArgumentParser<?, ?> parser = registry.getParser(TypeSignature.of(Player.class));
 * }</pre>
 *
 * @param <T> the type of the platform's original source object
 *
 * @since 0.0.10
 */
public abstract class BitsArgumentRegistry<T> {
    /**
     * Classes excluded here have no no-arg constructor and are instantiated manually instead of via classpath scanning.
     */
    private static final Predicate<Class<?>> CAN_AUTO_REGISTER = clazz -> clazz != GenericEnumParser.class;

    private final Map<TypeSignature<?>, ArgumentParser<?, ?>> parsers = new HashMap<>();

    public BitsArgumentRegistry() {
        List<ArgumentParser<?, ?>> initialParsers = new ArrayList<>(initialiseParsers().build());
        initialParsers.forEach(parser -> parsers.put(parser.getTypeSignature(), parser));
    }


    /**
     * Converts a basic type signature into a Brigadier {@link ArgumentType}.
     *
     * @param inputType the parsed type signature
     *
     * @return the corresponding Brigadier argument type, or null if it cannot be mapped trivially
     *
     * @since 0.0.10
     */
    protected @Nullable ArgumentType<?> toArgumentType(TypeSignature<?> inputType) {
        Class<?> clazz = inputType.toRawType();
        if (clazz == Integer.class || clazz == int.class) {
            return IntegerArgumentType.integer();
        } else if (clazz == Double.class || clazz == double.class) {
            return DoubleArgumentType.doubleArg();
        } else if (clazz == Float.class || clazz == float.class) {
            return FloatArgumentType.floatArg();
        } else if (clazz == Long.class || clazz == long.class) {
            return LongArgumentType.longArg();
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return BoolArgumentType.bool();
        } else if (clazz == GreedyString.class) {
            return StringArgumentType.greedyString();
        } else if (clazz == String.class) {
            return StringArgumentType.string();
        }

        return null;
    }

    /**
     * Initialises the list of abstract argument parsers provided by the implementation.
     *
     * @return a list of additional registered parsers
     *
     * @since 0.0.10
     */
    @SuppressWarnings("unchecked")
    protected AddableSet<ArgumentParser<?, ?>> initialiseParsers() {
        Set<ArgumentParser<?, ?>> parsers = (Set<ArgumentParser<?, ?>>)ReflectionUtils.Scanner.tryGetClasses("*", ArgumentParser.class, ScannerFlags.DEFAULT).stream()
          .filter(CAN_AUTO_REGISTER)
          .map(ReflectionUtils.Instance::tryCreate)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toSet());
        return AddableSet.of(parsers);
    }

    /**
     * Retrieves the appropriate parser for the given type signature.
     *
     * @param typeSignature the type signature generated from the method parameter
     *
     * @return the argument parser associated with the type, falling back to enum parsing or void
     *
     * @since 0.0.10
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ArgumentParser<?, ?> getParser(TypeSignature<?> typeSignature) {
        // We could consider implementing some form of search for inherited types.
        // This probably shouldn't be implemented as it'll cause type inconsistencies with functions.
        // Developers should design their command functions accordingly to use the lowest available type.
        ArgumentParser<?, ?> parser = parsers.get(typeSignature);

        // Exact HashMap lookup misses wildcard type arguments (e.g. Collection<? extends Player>);
        // fall back to a wildcard-tolerant scan before giving up.
        if (parser == null) {
            for (Map.Entry<TypeSignature<?>, ArgumentParser<?, ?>> entry : parsers.entrySet()) {
                if (entry.getKey().matches(typeSignature)) {
                    parser = entry.getValue();
                    break;
                }
            }
        }

        // If no parser found, we allow generic enums to be parsed.
        if (parser == null) {
            Class<?> rawType = typeSignature.toRawType();
            if (rawType.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>)rawType;
                return new GenericEnumParser<>(enumClass);
            }

            Logger.error("No parser registered for type: " + typeSignature);
            return VoidArgumentParser.INSTANCE;
        }

        return parser;
    }

    /**
     * Recursively constructs the required Brigadier argument nodes based on the input types of the parser.
     *
     * @param parser   the argument parser
     * @param baseName the base parameter name assigned in the command syntax
     *
     * @return a list of mappings directly translatable to Brigadier argument builders
     *
     * @since 0.0.10
     */
    public List<BrigadierArgumentMapping> getArgumentTypeContainer(ArgumentParser<?, ?> parser, String baseName) {
        // Terminal parsers are backed directly by a single Brigadier primitive - resolve from the
        // parser's own type signature rather than decomposing it, since decomposing a terminal parser
        // (e.g. GreedyStringArgumentParser, whose data class is String but type signature is GreedyString)
        // and re-looking it up by that data class would resolve the wrong parser entirely.
        if (isTerminal(parser)) {
            ArgumentType<?> brigadierType = toArgumentType(parser.getTypeSignature());
            if (brigadierType == null) throw new CommandBuildException("No Brigadier argument type mapped for: " + parser.getTypeSignature());
            return List.of(new BrigadierArgumentMapping(brigadierType, parser.getTypeSignature(), baseName));
        }

        List<BrigadierArgumentMapping> holders = new ArrayList<>();
        List<InputTypeContainer> inputTypes = parser.getInputTypes();

        // Break down the type signature into its constituent fields.
        for (int i = 0; i < inputTypes.size(); i++) {
            InputTypeContainer nestedTypeSigature = inputTypes.get(i);

            // Get the command parser required for this input type
            ArgumentParser<?, ?> nestedParser = getParser(nestedTypeSigature.typeSignature());

            String argumentName = inputTypes.size() > 1
                                  ? baseName + "_" + nestedTypeSigature.typeName()
                                  : baseName;

            holders.addAll(getArgumentTypeContainer(nestedParser, argumentName));
        }

        return holders;
    }

    // A terminal parser is backed directly by a raw Brigadier primitive value - its input list
    // is never decomposed further; the raw value is taken as-is.
    private static boolean isTerminal(ArgumentParser<?, ?> parser) {
        return parser instanceof PrimitiveArgumentParser<?> || parser instanceof GreedyStringArgumentParser;
    }

    /**
     * Parses a list of primitive objects recursively back into the complex object expected by the parser.
     *
     * @param parser        the argument parser invoked on the primitive inputs
     * @param primitiveList the list of parsed base arguments (such as Strings, Integers)
     * @param ctx           the context of the command execution
     *
     * @return the completely constructed complex object
     *
     * @throws CommandSyntaxException if there's an error in parsing validation
     * @since 0.0.10
     */
    public <O, D> O parseArguments(ArgumentParser<O, D> parser, List<Object> primitiveList, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        List<InputTypeContainer> inputTypes = parser.getInputTypes();
        List<Object> parsedObjects = new ArrayList<>();

        for (InputTypeContainer inputType : inputTypes) {
            ArgumentParser<?, ?> nestedParser = getParser(inputType.typeSignature());

            if (isTerminal(nestedParser)) {
                // it's a vanilla/terminal type (String, Double, GreedyString, etc.) take the primitive as-is.
                if (primitiveList.isEmpty()) throw new CommandBuildException("Not enough arguments for " + inputType.typeName());
                parsedObjects.add(primitiveList.removeFirst());
                continue;
            }

            int requiredSize = nestedParser.getInputTypes().size();
            if (primitiveList.size() < requiredSize) throw new CommandBuildException("Not enough arguments for " + inputType.typeName());

            List<Object> inputObjects = new ArrayList<>(primitiveList.subList(0, requiredSize));
            primitiveList = new ArrayList<>(primitiveList.subList(requiredSize, primitiveList.size()));

            Object parsedObject = parseArguments(nestedParser, inputObjects, ctx);
            parsedObjects.add(parsedObject);
        }

        D data = parser.toData(parsedObjects);
        return parser.parse(data, ctx);
    }

}
