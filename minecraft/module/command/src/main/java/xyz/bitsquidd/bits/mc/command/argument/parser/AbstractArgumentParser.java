/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.argument.InputTypeContainer;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A parser responsible for converting a list of raw command arguments into a specific object type.
 * <p>
 * This class serves as the foundation for all custom argument parsing within the Bits command framework.
 * Implementations define how string inputs or other basic types from Brigadier are structurally resolved
 * into complex objects, and optionally provide tab-completion suggestions.
 * <p>
 * Example parser implementation:
 * <pre>{@code
 * public class PlayerArgumentParser extends AbstractArgumentParser<Player> {
 *     public PlayerArgumentParser() {
 *         super(TypeSignature.of(Player.class), "player_name");
 *     }
 *
 *     // Override methods to dictate parsing logic and input types.
 * }
 * }</pre>
 *
 * @param <O> The type this parser converts to.
 *
 * @since 0.0.10
 */
public abstract class AbstractArgumentParser<O> {
    private final TypeSignature<?> typeSignature; // The type signature this parser handles
    private final String argumentName;            // The name of the argument, used while displaying suggestions

    /**
     * @param typeSignature the expected type this parser handles
     * @param argumentName  the label to display in command syntax suggestions
     *
     * @since 0.0.10
     */
    protected AbstractArgumentParser(TypeSignature<?> typeSignature, String argumentName) {
        this.typeSignature = typeSignature;
        this.argumentName = argumentName;
    }


    /**
     * Parses the provided primitive objects into the target complex object type {@code O}.
     *
     * @param inputObjects the raw inputs captured from the command context
     * @param ctx          the command execution context
     *
     * @return the successfully parsed object
     *
     * @throws CommandSyntaxException if the input is malformed or invalid
     * @since 0.0.10
     */
    public abstract O parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException;

    /**
     * Returns a list of required objects the parser expects in.
     * In the case of most custom parsers this will be a String.
     * <p>
     * In the case a <a href="https://docs.papermc.io/paper/dev/command-api/basics/arguments-and-literals/#arguments">non-vanilla primitive</a> is passed in, we expect a parser to be present for this.<p>
     * For example: <ul>
     * <li> An Integer parser would expect a single int {@code List.of(Integer.class)} </li>
     * <li> A <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Location.html">Bukkit Location</a> parser may expect three doubles and a World {@code List.of(Double.class, Double.class, Double.class, World.class)} </li>
     * </ul>
     *
     * @return A list of expected required objects.
     *
     * @since 0.0.10
     */
    public List<InputTypeContainer> getInputTypes() {
        return List.of(new InputTypeContainer(TypeSignature.of(String.class), getArgumentName()));
    }


    //region Validation

    /**
     * Helper function to validate singleton inputs for basic argument parsers.
     *
     * @param <I>          the type parameter of the expected output
     * @param inputObjects The list of input objects to validate, expected to contain exactly one object.
     * @param expectedType The expected type of the single input object.
     *
     * @return The validated input object cast to the expected type.
     *
     * @throws CommandSyntaxException if the validation fails
     * @since 0.0.10
     */
    protected <I> I singletonInputValidation(List<Object> inputObjects, Class<I> expectedType) throws CommandSyntaxException {
        List<InputTypeContainer> inputTypes = getInputTypes();
        if (inputTypes.size() != 1) throw ExceptionBuilder.createCommandException("Expected exactly one input type, got " + inputTypes.size() + ".");
        if (inputTypes.getFirst().typeSignature().toRawType() != expectedType) {
            throw ExceptionBuilder.createCommandException("Expected input type signature to be " + expectedType.getSimpleName() + ", got " + inputTypes.getFirst().typeSignature().toRawType().getSimpleName() + ".");
        }

        if (inputObjects.size() != 1) throw ExceptionBuilder.createCommandException("Expected exactly one input object, got " + inputObjects.size() + ".");
        Object value = inputObjects.getFirst();

        if (!expectedType.isInstance(value)) {
            throw ExceptionBuilder.createCommandException("Expected input object of type " + expectedType.getSimpleName() + ", got " + value.getClass().getSimpleName() + ".");
        }

        return expectedType.cast(value);
    }

    /**
     * Helper function to validate multiple arguments for complex argument parsers.
     *
     * @param inputObjects The list of input objects to validate, expected to match the size and types of the list returned by {@link #getInputTypes()}.
     *
     * @return A list of validated input objects cast to their expected types.
     *
     * @throws CommandSyntaxException if the input size or types are incorrect
     * @since 0.0.10
     */
    protected List<Object> inputValidation(List<Object> inputObjects) throws CommandSyntaxException {
        List<InputTypeContainer> inputTypes = getInputTypes();
        int inputSize = inputTypes.size();

        if (inputObjects.size() != inputSize) {
            throw ExceptionBuilder.createCommandException("Expected exactly " + inputSize + " input object" + (inputSize > 1 ? "s" : "") + ", got " + inputObjects.size() + ".");
        }

        List<Object> returnList = new ArrayList<>();

        for (int i = 0; i < inputSize; i++) {
            InputTypeContainer expectedTypeContainer = inputTypes.get(i);
            Object value = inputObjects.get(i);
            Class<?> expectedType = expectedTypeContainer.typeSignature().toRawType();

            if (!expectedType.isInstance(value)) {
                throw ExceptionBuilder.createCommandException("Expected input object of type " + expectedType.getSimpleName() + ", got " + value.getClass().getSimpleName() + ".");
            }

            returnList.add(expectedType.cast(value));
        }

        return returnList;
    }
    //endregion


    /**
     * Retrieves a Brigadier compatible suggestion provider for this argument parser.
     * <p>
     * This method evaluates {@link #getSuggestions()} to dynamically supply tab completion results.
     *
     * @param <T> the command source type
     *
     * @return the suggestion provider context for Brigadier nodes
     *
     * @since 0.0.10
     */
    public final <T> SuggestionProvider<T> getSuggestionProvider() {
        return (ctx, builder) -> {
            Supplier<List<String>> suggestionSupplier = getSuggestions();
            if (suggestionSupplier == null) return builder.buildFuture();

            List<String> suggestions = suggestionSupplier.get();
            String remaining = builder.getRemaining().toLowerCase();

            for (String suggestion : suggestions) {
                if (suggestion.toLowerCase().startsWith(remaining)) {
                    builder.suggest(suggestion);
                }
            }

            return builder.buildFuture();
        };
    }

    /**
     * Returns a dynamically loaded list of suggestions for the argument at a given context state.
     *
     * @return a supplier providing available string suggestions, or null if none exist
     *
     * @since 0.0.10
     */
    public @Nullable Supplier<List<String>> getSuggestions() {
        return null;
    }


    /**
     * Retrieves the core type signature handled by this parser.
     *
     * @return the type signature
     *
     * @since 0.0.10
     */
    public TypeSignature<?> getTypeSignature() {
        return typeSignature;
    }

    /**
     * Retrieves the argument name meant for display in syntax sequences.
     *
     * @return the argument label
     *
     * @since 0.0.10
     */
    public String getArgumentName() {
        return argumentName;
    }

}
