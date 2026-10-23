/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.Nullable;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Maps Java types to the {@link BrigadierArgument} that parses them. Primitive types use their wrapper's argument.
 * Enum types without a registered argument get one that matches constant names, ignoring case.
 *
 * @param <S> the command source type
 *
 * @since 0.1.6
 */
public final class BrigadierArguments<S> {

    private final Map<Class<?>, BrigadierArgument<S>> arguments = new ConcurrentHashMap<>();
    private final Map<Class<?>, BrigadierComposite<?>> composites = new ConcurrentHashMap<>();

    /**
     * @param <S> the command source type
     *
     * @return a registry with arguments for the built-in types: {@code String}, {@code Boolean}, {@code Integer},
     * {@code Long}, {@code Float} and {@code Double}
     *
     * @since 0.1.6
     */
    public static <S> BrigadierArguments<S> withDefaults() {
        BrigadierArguments<S> registry = new BrigadierArguments<>();
        registry.register(String.class, typed(String.class, StringArgumentType.word()));
        registry.register(Boolean.class, typed(Boolean.class, BoolArgumentType.bool()));
        registry.register(Integer.class, typed(Integer.class, IntegerArgumentType.integer()));
        registry.register(Long.class, typed(Long.class, LongArgumentType.longArg()));
        registry.register(Float.class, typed(Float.class, FloatArgumentType.floatArg()));
        registry.register(Double.class, typed(Double.class, DoubleArgumentType.doubleArg()));
        return registry;
    }

    /**
     * Registers the argument for a type, replacing any existing one.
     *
     * @param type     the Java type, not primitive
     * @param argument the argument that parses it
     *
     * @since 0.1.6
     */
    public void register(Class<?> type, BrigadierArgument<S> argument) {
        arguments.put(type, argument);
    }

    /**
     * Registers a type that is read from several arguments, replacing any existing one.
     *
     * @param type      the composite Java type
     * @param composite the parts and how they combine
     *
     * @since 0.1.6
     */
    public void registerComposite(Class<?> type, BrigadierComposite<?> composite) {
        composites.put(type, composite);
    }

    /**
     * @param type the Java type
     *
     * @return the composite for the type, or {@code null} if the type is read from a single argument
     *
     * @since 0.1.6
     */
    public @Nullable BrigadierComposite<?> composite(Class<?> type) {
        return composites.get(type);
    }

    /**
     * @param type the Java type, primitive or wrapper
     *
     * @return the argument for the type
     *
     * @throws IllegalArgumentException if no argument handles the type
     *
     * @since 0.1.6
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public BrigadierArgument<S> get(Class<?> type) {
        Class<?> key = ReflectionUtils.Primitive.fromOrSelf(type);
        BrigadierArgument<S> argument = arguments.get(key);

        if (argument == null && key.isEnum()) {
            argument = arguments.computeIfAbsent(key, k -> enumOf((Class<? extends Enum>) k));
        }
        if (argument != null) return argument;
        throw new IllegalArgumentException("No Brigadier argument registered for type: " + type.getName());
    }

    private static <S, T> BrigadierArgument<S> typed(Class<T> type, ArgumentType<T> argumentType) {
        return new BrigadierArgument<>() {
            @Override
            public ArgumentType<?> argumentType() {
                return argumentType;
            }

            @Override
            public Object read(CommandContext<S> context, String name) {
                return context.getArgument(name, type);
            }
        };
    }

    private static <S, E extends Enum<E>> BrigadierArgument<S> enumOf(Class<E> type) {
        SuggestionProvider<S> suggestions = (context, builder) -> {
            Arrays.stream(type.getEnumConstants())
              .map(constant -> constant.name().toLowerCase(Locale.ROOT))
              .forEach(builder::suggest);
            return builder.buildFuture();
        };

        return new BrigadierArgument<>() {
            @Override
            public ArgumentType<?> argumentType() {
                return StringArgumentType.word();
            }

            @Override
            public Object read(CommandContext<S> context, String name) {
                return Enum.valueOf(type, context.getArgument(name, String.class).toUpperCase(Locale.ROOT));
            }

            @Override
            public SuggestionProvider<S> suggestions() {
                return suggestions;
            }
        };
    }

}
