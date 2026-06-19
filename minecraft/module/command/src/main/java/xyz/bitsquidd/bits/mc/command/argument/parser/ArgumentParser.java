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

import xyz.bitsquidd.bits.mc.command.BitsCommandManager;
import xyz.bitsquidd.bits.mc.command.argument.InputTypeContainer;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.GreedyStringArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.GreedyString;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


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
 * @param <D> The type of the input data (e.g., String, Integer, World) this parser expects to receive from Brigadier or parser.
 *
 * @since 0.0.10
 */
public abstract class ArgumentParser<O, D> {
    private final TypeSignature<?> typeSignature;  // The type signature this parser handles
    private final String argumentName;             // The name of the argument, used while displaying suggestions
    private final Class<D> dataClass;              // The class of the input data type
    private final @Nullable Constructor<D> recordConstructor;

    /**
     * @param typeSignature the expected type this parser handles
     * @param argumentName  the label to display in command syntax suggestions
     * @param dataClass     the class of the input data type
     *
     * @since 0.0.10
     */
    protected ArgumentParser(TypeSignature<?> typeSignature, String argumentName, Class<D> dataClass) {
        this.typeSignature = typeSignature;
        this.argumentName = argumentName;
        this.dataClass = dataClass;
        this.recordConstructor = dataClass.isRecord() ? resolveCanonicalConstructor(dataClass) : null;
    }


    private static <D> Constructor<D> resolveCanonicalConstructor(Class<D> recordClass) {
        Class<?>[] paramTypes = Arrays.stream(recordClass.getRecordComponents())
          .map(RecordComponent::getType)
          .toArray(Class<?>[]::new);

        try {
            return recordClass.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            // Unreachable: every record has a canonical constructor.
            throw new IllegalStateException("No canonical constructor found for record " + recordClass.getSimpleName(), e);
        }
    }


    /**
     * Parses the provided primitive objects into the target complex object type {@code O}.
     *
     * @param data the raw inputs captured from the command context
     * @param ctx  the command execution context
     *
     * @return the successfully parsed object
     *
     * @throws CommandSyntaxException if the input is malformed or invalid
     * @since 0.0.10
     */
    public abstract O parse(D data, BitsCommandContext<?> ctx) throws CommandSyntaxException;

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
     * @since 0.0.20
     */
    public final List<InputTypeContainer> getInputTypes() {
        // Special case for GreedyString: it captures all remaining input as a single string.
        if (this instanceof GreedyStringArgumentParser) return List.of(new InputTypeContainer(TypeSignature.of(GreedyString.class), getArgumentName()));

        // Special case for non-record types: treat the entire data class the input.
        if (!dataClass.isRecord()) return List.of(new InputTypeContainer(TypeSignature.of(dataClass), argumentName));

        RecordComponent[] components = dataClass.getRecordComponents();
        List<InputTypeContainer> inputTypes = new ArrayList<>(components.length);

        for (RecordComponent component : components) {
            inputTypes.add(new InputTypeContainer(TypeSignature.of(component.getType()), component.getName()));
        }

        return inputTypes;
    }


    public final D toData(List<Object> inputObjects) throws CommandSyntaxException {
        List<InputTypeContainer> inputTypes = getInputTypes();

        if (inputObjects.size() != inputTypes.size()) throw ExceptionBuilder.createCommandException("Expected exactly " + inputTypes.size() + " input object" + (inputTypes.size() > 1 ? "s" : "") + ", got " + inputObjects.size() + ".");
        Object[] validated = new Object[inputTypes.size()];

        for (int i = 0; i < inputTypes.size(); i++) {
            Class<?> expectedType = inputTypes.get(i).typeSignature().toRawType();
            Object value = inputObjects.get(i);

            if (!expectedType.isInstance(value)) throw ExceptionBuilder.createCommandException("Expected input object of type " + expectedType.getSimpleName() + ", got " + value.getClass().getSimpleName() + ".");
            validated[i] = value;
        }

        // Single-value case: D itself is the scalar type (e.g. D = Double)
        if (!dataClass.isRecord()) return dataClass.cast(validated[0]);

        try {
            return Objects.requireNonNull(this.recordConstructor).newInstance(validated);
        } catch (ReflectiveOperationException e) {
            throw ExceptionBuilder.createCommandException("Failed to construct " + dataClass.getSimpleName() + ": " + e.getMessage());
        }
    }


    /**
     * Retrieves a Brigadier compatible suggestion provider for this argument parser.
     * <p>
     * This method evaluates {@link #getSuggestions} to dynamically supply tab completion results.
     *
     * @param <T> the command source type
     *
     * @return the suggestion provider context for Brigadier nodes
     *
     * @since 0.0.10
     */
    public final <T> SuggestionProvider<T> createSuggestionProvider() {
        SuggestionSupplier<T> suggestionSupplier = getSuggestions();
        if (suggestionSupplier == null) return (ctx, builder) -> builder.buildFuture(); // No suggestions available
        BitsCommandManager<T> manager = BitsCommandManager.get();

        return (ctx, builder) -> {
            List<String> suggestions = suggestionSupplier.getSuggestions(manager.createContext(ctx));
            String remaining = builder.getRemaining();

            for (String suggestion : suggestions) {
                if (suggestion.toLowerCase().startsWith(remaining.toLowerCase())) {
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
    public <T> @Nullable SuggestionSupplier<T> getSuggestions() {
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
