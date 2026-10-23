/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jetbrains.annotations.Nullable;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandDefinition.Segment;
import xyz.orbitary.bits.module.command.Condition;
import xyz.orbitary.bits.module.command.ParameterDefinition;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Builds Brigadier command trees from {@link CommandDefinition}s and runs the matching command methods.
 * <p>
 * Each group becomes a literal node. A group's constructor arguments come after its literal, before its subcommands,
 * and the group instance is created from them. Each command ends in a node that runs it, and optional arguments let
 * it run at the node before them too. Aliases become extra literals for the same node. A failed condition or an error
 * is passed to the error handler as an exception, and the command returns {@code 0}. Conditions never hide commands.
 *
 * @param <S> the command source type
 *
 * @since 0.1.6
 */
public final class BrigadierCommandBuilder<S> {

    private final BrigadierArguments<S> arguments;
    private final Function<CommandContext<S>, ?> contextFactory;
    private final BiConsumer<S, Throwable> errorHandler;
    private final Executor async;

    /**
     * @param arguments      the arguments that parse each parameter type
     * @param contextFactory creates the value for parameters the platform injects, such as a context object
     * @param errorHandler   receives the source and the error of a command that did not run
     * @param async          runs commands marked {@code @Async}
     *
     * @since 0.1.6
     */
    public BrigadierCommandBuilder(
      BrigadierArguments<S> arguments,
      Function<CommandContext<S>, ?> contextFactory,
      BiConsumer<S, Throwable> errorHandler,
      Executor async
    ) {
        this.arguments = arguments;
        this.contextFactory = contextFactory;
        this.errorHandler = errorHandler;
        this.async = async;
    }

    /**
     * @param commands the commands to build, as read by {@code CommandScanner}
     *
     * @return the root literal of each command group and alias, ready to register with a Brigadier dispatcher
     *
     * @throws IllegalArgumentException if a parameter type has no Brigadier argument, a composite is optional, a
     *                                  greedy argument is not last, or a suggestion does not fit its composite
     *
     * @since 0.1.6
     */
    public List<LiteralArgumentBuilder<S>> build(List<CommandDefinition> commands) {
        Node tree = new Node();
        for (CommandDefinition command : commands) {
            Node node = tree;
            for (Segment segment : command.path()) {
                node = node.children.computeIfAbsent(segment.name(), name -> new Node());
                node.parameters = segment.parameters();
                node.aliases = segment.aliases();
            }
            node.command = command;
        }
        return literals(tree.children);
    }

    // Brigadier copies a builder when it is added with then(), so children are built before their parent is added.
    private List<LiteralArgumentBuilder<S>> literals(Map<String, Node> children) {
        List<LiteralArgumentBuilder<S>> literals = new ArrayList<>();
        children.forEach((name, node) -> {
            literals.add(literal(name, node));
            for (String alias : node.aliases) literals.add(literal(alias, node));
        });
        return literals;
    }

    private LiteralArgumentBuilder<S> literal(String name, Node node) {
        LiteralArgumentBuilder<S> literal = LiteralArgumentBuilder.literal(name);

        List<Element<S>> groupElements = elements(node.parameters);
        for (int i = 0; i < groupElements.size(); i++) {
            if (isGreedy(groupElements.get(i).argument())) {
                throw new IllegalArgumentException("Greedy argument '" + groupElements.get(i).name() + "' cannot be a group argument of /" + name);
            }
        }

        CommandDefinition command = node.command;
        List<Element<S>> methodElements = command == null ? List.of() : elements(command.parameters());
        if (command != null) checkGreedy(methodElements, command.fullName());

        Command<S> execution = command == null ? null : context -> run(command, context);
        ArgumentBuilder<S, ?> methodTop = chain(methodElements, execution);
        Command<S> tailExecution = execution != null && noneRequiredFrom(methodElements, 0) ? execution : null;
        List<LiteralArgumentBuilder<S>> children = literals(node.children);

        if (groupElements.isEmpty()) {
            attach(literal, children, methodTop, tailExecution);
            return literal;
        }

        // The group's arguments form a chain under the literal. The subcommands and the group's own command hang
        // off its last argument.
        ArgumentBuilder<S, ?> top = null;
        for (int i = groupElements.size() - 1; i >= 0; i--) {
            RequiredArgumentBuilder<S, ?> argument = argument(groupElements.get(i));
            if (i == groupElements.size() - 1) {
                attach(argument, children, methodTop, tailExecution);
            } else {
                argument.then(top);
            }
            top = argument;
        }
        literal.then(top);
        return literal;
    }

    private void attach(ArgumentBuilder<S, ?> builder, List<LiteralArgumentBuilder<S>> children, @Nullable ArgumentBuilder<S, ?> methodTop, @Nullable Command<S> execution) {
        if (execution != null) builder.executes(execution);
        for (LiteralArgumentBuilder<S> child : children) builder.then(child);
        if (methodTop != null) builder.then(methodTop);
    }

    // Builds the arguments of one command as a chain. Each argument runs the command when the rest are optional.
    private @Nullable ArgumentBuilder<S, ?> chain(List<Element<S>> elements, @Nullable Command<S> execution) {
        ArgumentBuilder<S, ?> top = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            RequiredArgumentBuilder<S, ?> argument = argument(elements.get(i));
            if (execution != null && noneRequiredFrom(elements, i + 1)) argument.executes(execution);
            if (top != null) argument.then(top);
            top = argument;
        }
        return top;
    }

    private static <S> void checkGreedy(List<Element<S>> elements, String command) {
        for (int i = 0; i < elements.size() - 1; i++) {
            if (isGreedy(elements.get(i).argument())) {
                throw new IllegalArgumentException("Greedy argument '" + elements.get(i).name() + "' must be the last argument of /" + command);
            }
        }
    }

    // A composite parameter becomes one element per part, so the parts are read and parsed in order.
    private List<Element<S>> elements(List<ParameterDefinition> parameters) {
        List<Element<S>> elements = new ArrayList<>();
        for (ParameterDefinition parameter : parameters) {
            BrigadierComposite<?> composite = arguments.composite(parameter.rawType());
            if (composite == null) {
                elements.add(new Element<>(parameter.name(), arguments.get(parameter.type()), parameter.required(), parameter.suggestions()));
                continue;
            }
            if (!parameter.required()) {
                throw new IllegalArgumentException("Composite argument '" + parameter.name() + "' cannot be optional");
            }
            List<List<String>> partSuggestions = partSuggestions(parameter, composite.parts().size());
            for (int i = 0; i < composite.parts().size(); i++) {
                BrigadierComposite.Part part = composite.parts().get(i);
                elements.add(new Element<>(partName(parameter, part), arguments.get(part.type()), true, partSuggestions.get(i)));
            }
        }
        return elements;
    }

    // Each @Suggest value of a composite holds one token per part, so the tokens of a value complete together.
    private static List<List<String>> partSuggestions(ParameterDefinition parameter, int parts) {
        List<List<String>> columns = new ArrayList<>();
        for (int i = 0; i < parts; i++) columns.add(new ArrayList<>());
        for (String value : parameter.suggestions()) {
            String[] tokens = value.trim().split(" +");
            if (tokens.length != parts) {
                throw new IllegalArgumentException("@Suggest value '" + value + "' for composite argument '" + parameter.name() + "' must have " + parts + " parts");
            }
            for (int i = 0; i < parts; i++) columns.get(i).add(tokens[i]);
        }
        return columns.stream().map(List::copyOf).toList();
    }

    private RequiredArgumentBuilder<S, ?> argument(Element<S> element) {
        RequiredArgumentBuilder<S, ?> builder = RequiredArgumentBuilder.argument(element.name(), element.argument().argumentType());
        SuggestionProvider<S> suggestions = suggestions(element);
        if (suggestions != null) builder.suggests(suggestions);
        return builder;
    }

    private static boolean isGreedy(BrigadierArgument<?> argument) {
        return argument.greedy()
          || argument.argumentType() instanceof StringArgumentType string && string.getType() == StringArgumentType.StringType.GREEDY_PHRASE;
    }

    private static <S> boolean noneRequiredFrom(List<Element<S>> elements, int from) {
        return elements.stream().skip(from).noneMatch(Element::required);
    }

    private static String partName(ParameterDefinition parameter, BrigadierComposite.Part part) {
        return parameter.name() + "_" + part.name();
    }

    private int run(CommandDefinition command, CommandContext<S> context) {
        if (command.async()) {
            async.execute(() -> execute(command, context));
            return 1;
        }
        return execute(command, context);
    }

    private int execute(CommandDefinition command, CommandContext<S> context) {
        S source = context.getSource();
        try {
            Condition<S> failed = failedCondition(command, source);
            if (failed != null) throw new ConditionFailedException(failed.getClass());

            Object owner = command.constructor().newInstance(resolveArguments(command.constructor(), command.constructorParameters(), context));
            command.method().invoke(owner, resolveArguments(command.method(), command.parameters(), context));
            return 1;
        } catch (CommandSyntaxException | ConditionFailedException e) {
            errorHandler.accept(source, e);
            return 0;
        } catch (Exception e) {
            Throwable cause = e instanceof InvocationTargetException && e.getCause() != null ? e.getCause() : e;
            Logger.exception("Unhandled exception in command /" + command.fullName(), cause);
            errorHandler.accept(source, cause);
            return 0;
        }
    }

    private static <S> @Nullable Condition<S> failedCondition(CommandDefinition command, S source) {
        for (Class<? extends Condition<?>> conditionClass : command.conditions()) {
            Condition<S> condition = conditionOf(conditionClass);
            if (!condition.test(source)) return condition;
        }
        return null;
    }

    private Object[] resolveArguments(Executable executable, List<ParameterDefinition> parameters, CommandContext<S> context) throws CommandSyntaxException {
        Object[] args = new Object[executable.getParameterCount()];

        Set<Integer> argumentIndices = parameters.stream().map(ParameterDefinition::index).collect(Collectors.toSet());
        if (argumentIndices.size() < args.length) {
            Object injected = contextFactory.apply(context);
            for (int i = 0; i < args.length; i++) {
                if (!argumentIndices.contains(i)) args[i] = injected;
            }
        }

        Set<String> present = context.getNodes().stream().map(parsed -> parsed.getNode().getName()).collect(Collectors.toSet());
        for (ParameterDefinition parameter : parameters) {
            BrigadierComposite<?> composite = arguments.composite(parameter.rawType());
            if (composite != null) {
                Object[] parts = new Object[composite.parts().size()];
                for (int i = 0; i < parts.length; i++) {
                    BrigadierComposite.Part part = composite.parts().get(i);
                    parts[i] = arguments.get(part.type()).read(context, partName(parameter, part));
                }
                args[parameter.index()] = composite.combiner().apply(parts);
            } else if (present.contains(parameter.name())) {
                args[parameter.index()] = arguments.get(parameter.type()).read(context, parameter.name());
            }
        }
        return args;
    }

    @SuppressWarnings("unchecked")
    private static <S> Condition<S> conditionOf(Class<? extends Condition<?>> type) {
        return (Condition<S>) instantiate(type);
    }

    private static Object instantiate(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot instantiate " + type.getName(), e);
        }
    }

    // Completions run in order: the argument's own type, then the fixed @Suggest values, then the argument's extra provider.
    private static <S> @Nullable SuggestionProvider<S> suggestions(Element<S> element) {
        SuggestionProvider<S> own = element.argument().suggestions();
        List<String> extra = element.suggestions();
        if (own == null && extra.isEmpty()) return null;

        ArgumentType<?> type = element.argument().argumentType();
        List<SuggestionProvider<S>> extras = new ArrayList<>();
        if (!extra.isEmpty()) extras.add(BrigadierArgument.<S>suggest(extra));
        if (own != null) extras.add(own);

        // Every step adds to the same builder, so the final build holds the native and the extra completions.
        return (context, builder) -> {
            CompletableFuture<Suggestions> result = type.listSuggestions(context, builder);
            for (SuggestionProvider<S> provider : extras) {
                result = result.thenCompose(ignored -> complete(provider, context, builder));
            }
            return result.thenCompose(ignored -> builder.buildFuture());
        };
    }

    private static <S> CompletableFuture<Suggestions> complete(SuggestionProvider<S> provider, CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            return provider.getSuggestions(context, builder);
        } catch (CommandSyntaxException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private record Element<T>(String name, BrigadierArgument<T> argument, boolean required, List<String> suggestions) {}

    private static final class Node {
        private final Map<String, Node> children = new LinkedHashMap<>();
        private @Nullable CommandDefinition command;
        private List<ParameterDefinition> parameters = List.of();
        private List<String> aliases = List.of();
    }

}
