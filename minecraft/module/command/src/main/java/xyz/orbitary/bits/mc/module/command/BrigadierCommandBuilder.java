/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import org.jetbrains.annotations.Nullable;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandDefinition.Segment;
import xyz.orbitary.bits.module.command.Condition;
import xyz.orbitary.bits.module.command.ParameterDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Builds Brigadier command trees from {@link CommandDefinition}s and runs the matching command methods.
 * <p>
 * Each group becomes a literal node and each command ends in a node that runs it. Optional arguments
 * let the command run at the node before them too. Commands with conditions are hidden from sources that
 * fail them, as vanilla commands are. An error is reported through the feedback consumer and returns {@code 0}.
 *
 * @param <S> the command source type
 *
 * @since 0.1.6
 */
public final class BrigadierCommandBuilder<S> {

    private static final String INTERNAL_ERROR = "An error occurred while running this command.";

    private final BrigadierArguments<S> arguments;
    private final Function<CommandContext<S>, ?> contextFactory;
    private final BiConsumer<S, String> feedback;
    private final Executor async;

    /**
     * @param arguments      the arguments that parse each parameter type
     * @param contextFactory creates the value for parameters the platform injects, such as a context object
     * @param feedback       sends an error message to the source
     * @param async          runs commands marked {@code @Async}
     *
     * @since 0.1.6
     */
    public BrigadierCommandBuilder(
      BrigadierArguments<S> arguments,
      Function<CommandContext<S>, ?> contextFactory,
      BiConsumer<S, String> feedback,
      Executor async
    ) {
        this.arguments = arguments;
        this.contextFactory = contextFactory;
        this.feedback = feedback;
        this.async = async;
    }

    /**
     * @param commands the commands to build, as read by {@code CommandScanner}
     *
     * @return the root literal of each command group, ready to register with a Brigadier dispatcher
     *
     * @throws IllegalArgumentException if a parameter type has no Brigadier argument, or a composite is optional
     *
     * @since 0.1.6
     */
    public List<LiteralArgumentBuilder<S>> build(List<CommandDefinition> commands) {
        Node tree = new Node();
        for (CommandDefinition command : commands) {
            Node node = tree;
            for (Segment segment : command.path()) {
                node = node.children.computeIfAbsent(segment.name(), name -> new Node());
            }
            node.command = command;
        }
        return tree.children.entrySet().stream()
          .map(entry -> literal(entry.getKey(), entry.getValue()))
          .toList();
    }

    // Brigadier copies a builder when it is added with then(), so children are built before their parent is added.
    private LiteralArgumentBuilder<S> literal(String name, Node node) {
        LiteralArgumentBuilder<S> literal = LiteralArgumentBuilder.literal(name);
        CommandDefinition command = node.command;
        if (command != null) {
            attachArguments(literal, command);
            if (!command.conditions().isEmpty()) literal.requires(source -> failedCondition(command, source) == null);
        }
        node.children.forEach((childName, child) -> literal.then(literal(childName, child)));
        return literal;
    }

    private void attachArguments(LiteralArgumentBuilder<S> literal, CommandDefinition command) {
        List<Element<S>> elements = elements(command.parameters());
        Command<S> execution = context -> run(command, context);

        if (noneRequiredFrom(elements, 0)) literal.executes(execution);

        ArgumentBuilder<S, ?> tail = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            RequiredArgumentBuilder<S, ?> argument = argument(elements.get(i));
            if (noneRequiredFrom(elements, i + 1)) argument.executes(execution);
            if (tail != null) argument.then(tail);
            tail = argument;
        }
        if (tail != null) literal.then(tail);
    }

    // A composite parameter becomes one element per part, so the parts are read and parsed in order.
    private List<Element<S>> elements(List<ParameterDefinition> parameters) {
        List<Element<S>> elements = new ArrayList<>();
        for (ParameterDefinition parameter : parameters) {
            BrigadierComposite<?> composite = arguments.composite(parameter.type());
            if (composite == null) {
                elements.add(new Element<>(parameter.name(), arguments.get(parameter.type()), parameter.required()));
                continue;
            }
            if (!parameter.required()) {
                throw new IllegalArgumentException("Composite argument '" + parameter.name() + "' cannot be optional");
            }
            for (BrigadierComposite.Part part : composite.parts()) {
                elements.add(new Element<>(partName(parameter, part), arguments.get(part.type()), true));
            }
        }
        return elements;
    }

    private RequiredArgumentBuilder<S, ?> argument(Element<S> element) {
        RequiredArgumentBuilder<S, ?> builder = RequiredArgumentBuilder.argument(element.name(), element.argument().argumentType());
        if (element.argument().suggestions() != null) builder.suggests(element.argument().suggestions());
        return builder;
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
            if (failedCondition(command, source) != null) return 0;

            Object owner = instantiate(command.owner());
            command.method().invoke(owner, resolveArguments(command, context));
            return 1;
        } catch (Exception e) {
            Logger.exception("Unhandled exception in command /" + command.fullName(), e);
            feedback.accept(source, INTERNAL_ERROR);
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

    private Object[] resolveArguments(CommandDefinition command, CommandContext<S> context) {
        Method method = command.method();
        Object[] args = new Object[method.getParameterCount()];

        Set<Integer> argumentIndices = command.parameters().stream().map(ParameterDefinition::index).collect(Collectors.toSet());
        if (argumentIndices.size() < args.length) {
            Object injected = contextFactory.apply(context);
            for (int i = 0; i < args.length; i++) {
                if (!argumentIndices.contains(i)) args[i] = injected;
            }
        }

        Set<String> present = context.getNodes().stream().map(parsed -> parsed.getNode().getName()).collect(Collectors.toSet());
        for (ParameterDefinition parameter : command.parameters()) {
            BrigadierComposite<?> composite = arguments.composite(parameter.type());
            if (composite != null) {
                Object[] parts = composite.parts().stream()
                  .map(part -> arguments.get(part.type()).read(context, partName(parameter, part)))
                  .toArray();
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

    private record Element<T>(String name, BrigadierArgument<T> argument, boolean required) {}

    private static final class Node {
        private final Map<String, Node> children = new LinkedHashMap<>();
        private @Nullable CommandDefinition command;
    }

}
