/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.GreedyStringArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.LinkArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.UUIDArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.VoidArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.generic.GenericEnumParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive.*;
import xyz.bitsquidd.bits.mc.command.exception.CommandBuildException;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.GreedyString;
import xyz.bitsquidd.bits.wrapper.collection.AddableSet;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * AbstractArgumentParser<?> parser = registry.getParser(TypeSignature.of(Player.class));
 * }</pre>
 *
 * @param <T> the type of the platform's original source object
 *
 * @since 0.0.10
 */
public abstract class BitsArgumentRegistry<T> {
    private final Map<TypeSignature<?>, AbstractArgumentParser<?>> parsers = new HashMap<>();

    public BitsArgumentRegistry() {
        List<AbstractArgumentParser<?>> initialParsers = new ArrayList<>(initialisePrimitiveParsers());
        initialParsers.addAll(initialiseParsers().build());
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
     * Initialises the list of base primitive argument parsers provided by the core API.
     *
     * @return a list of primitive parsers
     *
     * @since 0.0.10
     */
    protected List<PrimitiveArgumentParser<?>> initialisePrimitiveParsers() {
        return List.of(
          new BooleanArgumentParser(),
          new DoubleArgumentParser(),
          new FloatArgumentParser(),
          new IntegerArgumentParser(),
          new LongArgumentParser(),
          new StringArgumentParser()
        );
    }

    /**
     * Initialises the list of abstract argument parsers provided by the implementation.
     *
     * @return a list of additional registered parsers
     *
     * @since 0.0.10
     */
    protected AddableSet<AbstractArgumentParser<?>> initialiseParsers() {
        // Override to add custom parsers
        return AddableSet.of(
          new GreedyStringArgumentParser(),
          new LinkArgumentParser(),
          new UUIDArgumentParser()
        );
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
    public AbstractArgumentParser<?> getParser(TypeSignature<?> typeSignature) {
        // We could consider implementing some form of search for inherited types.
        // This probably shouldn't be implemented as it'll cause type inconsistencies with functions.
        // Developers should design their command functions accordingly to use the lowest available type.
        AbstractArgumentParser<?> parser = parsers.get(typeSignature);

        // If no parser found, we allow generic enums to be parsed.
        if (parser == null) {
            Class<?> rawType = typeSignature.toRawType();
            if (rawType.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>)rawType;
                return new GenericEnumParser<>(enumClass);
            }

            Logger.error("No parser registered for type: " + typeSignature);
            return new VoidArgumentParser();
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
    public List<BrigadierArgumentMapping> getArgumentTypeContainer(AbstractArgumentParser<?> parser, String baseName) {
        List<BrigadierArgumentMapping> holders = new ArrayList<>();
        List<InputTypeContainer> inputTypes = parser.getInputTypes();

        // Break down the type signature into its primitives.
        for (int i = 0; i < inputTypes.size(); i++) {
            InputTypeContainer nestedTypeSigature = inputTypes.get(i);
            // Get the command parser required for this input type
            AbstractArgumentParser<?> nestedParser = getParser(nestedTypeSigature.typeSignature());

            boolean handled = false;
            // If its a primitive, we can directly add it
            if (nestedParser.getInputTypes().size() == 1) {
                InputTypeContainer inputType = nestedParser.getInputTypes().getFirst();
                ArgumentType<?> brigadierType = toArgumentType(inputType.typeSignature());

                if (brigadierType != null) {
                    String argumentName = inputTypes.size() > 1
                                          ? baseName + "_" + nestedTypeSigature.typeName()
                                          : baseName;

                    holders.add(new BrigadierArgumentMapping(
                      brigadierType,
                      inputType.typeSignature(),
                      argumentName
                    ));
                    handled = true;
                }
            }

            if (!handled) {
                // Recurse into non-primitive parsers
                holders.addAll(getArgumentTypeContainer(nestedParser, baseName + "_" + nestedTypeSigature.typeName()));
            }
        }

        return holders;
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
    public Object parseArguments(AbstractArgumentParser<?> parser, List<Object> primitiveList, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        List<InputTypeContainer> inputTypes = parser.getInputTypes();

        // If the input size is 1, we can directly parse it
        if (inputTypes.size() == 1) return parser.parse(primitiveList, ctx);

        List<Object> parsedObjects = new ArrayList<>();

        for (InputTypeContainer inputType : inputTypes) {
            AbstractArgumentParser<?> nestedParser = getParser(inputType.typeSignature());

            int requiredSize = nestedParser.getInputTypes().size();
            if (primitiveList.size() < requiredSize) throw new CommandBuildException("Not enough arguments for " + inputType.typeName());

            ArrayList<Object> inputObjects = new ArrayList<>(primitiveList.subList(0, requiredSize));
            primitiveList = new ArrayList<>(primitiveList.subList(requiredSize, primitiveList.size()));

            // Recursively parse the primitives with the appropriate parser
            Object parsedObject = parseArguments(nestedParser, inputObjects, ctx);
            parsedObjects.add(parsedObject);
        }

        // Now that we have all our parsed objects, we can pass them to the main parser
        return parser.parse(parsedObjects, ctx);
    }

}
