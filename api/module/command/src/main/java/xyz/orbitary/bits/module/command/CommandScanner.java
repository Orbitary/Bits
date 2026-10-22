/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import xyz.orbitary.bits.module.command.CommandDefinition.Segment;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Reads {@link CommandDefinition}s from the {@link Command} methods of a class and its nested {@link Command} classes.
 * <p>
 * Every command method must be public. Argument names are normalised to lower case with spaces replaced by
 * underscores. Required arguments may not follow optional ones.
 *
 * @since 0.1.6
 */
public final class CommandScanner {

    private CommandScanner() {}

    /**
     * Reads every command declared by {@code owner}, treating no parameter as injected.
     *
     * @param owner the class to scan
     *
     * @return the commands, or an empty list if there are none
     *
     * @throws IllegalArgumentException if a command is invalid or its path is already used
     *
     * @since 0.1.6
     */
    public static List<CommandDefinition> scan(Class<?> owner) {
        return scan(owner, type -> false);
    }

    /**
     * Reads every command declared by {@code owner}.
     *
     * @param owner    the class to scan
     * @param injected returns true for parameter types the platform supplies itself, such as a context object.
     *                 Those parameters are left out of the arguments.
     *
     * @return the commands, or an empty list if there are none
     *
     * @throws IllegalArgumentException if a command is invalid or its path is already used
     *
     * @since 0.1.6
     */
    public static List<CommandDefinition> scan(Class<?> owner, Predicate<Class<?>> injected) {
        List<CommandDefinition> commands = new ArrayList<>();
        collect(owner, List.of(), injected, commands);

        Set<List<String>> paths = new HashSet<>();
        for (CommandDefinition command : commands) {
            List<String> names = command.path().stream().map(Segment::name).toList();
            if (!paths.add(names)) {
                throw new IllegalArgumentException("Duplicate command '" + String.join(" ", names) + "' in " + owner.getName());
            }
        }
        return commands;
    }

    private static void collect(Class<?> owner, List<Segment> prefix, Predicate<Class<?>> injected, List<CommandDefinition> out) {
        Command group = owner.getAnnotation(Command.class);
        List<Segment> base = group == null ? prefix : append(prefix, segment(group, owner));

        for (Method method : owner.getDeclaredMethods()) {
            Command command = method.getAnnotation(Command.class);
            if (command != null) out.add(define(owner, method, command, base, injected));
        }
        for (Class<?> nested : owner.getDeclaredClasses()) {
            if (nested.isAnnotationPresent(Command.class)) collect(nested, base, injected, out);
        }
    }

    private static CommandDefinition define(Class<?> owner, Method method, Command command, List<Segment> base, Predicate<Class<?>> injected) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("Command method " + method + " must be public");
        }

        List<Segment> path;
        if (command.value().isBlank()) {
            // A blank name is only valid as the handler of a command group.
            if (!owner.isAnnotationPresent(Command.class)) throw new IllegalArgumentException("Command method " + method + " has a blank name");
            path = base;
        } else {
            path = append(base, segment(command, method));
        }

        return new CommandDefinition(
          path,
          owner,
          method,
          parameters(method, injected),
          method.isAnnotationPresent(Async.class) || owner.isAnnotationPresent(Async.class),
          conditions(owner, method)
        );
    }

    private static List<ParameterDefinition> parameters(Method method, Predicate<Class<?>> injected) {
        List<ParameterDefinition> parameters = new ArrayList<>();
        Set<String> names = new HashSet<>();
        boolean optionalSeen = false;

        java.lang.reflect.Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            if (injected.test(params[i].getType())) continue;

            ParameterDefinition parameter = defineParameter(i, params[i]);
            if (!names.add(parameter.name())) {
                throw new IllegalArgumentException("Duplicate argument '" + parameter.name() + "' in " + method);
            }
            if (!parameter.required()) {
                optionalSeen = true;
            } else if (optionalSeen) {
                throw new IllegalArgumentException("Required argument '" + parameter.name() + "' follows an optional argument in " + method);
            }
            parameters.add(parameter);
        }
        return List.copyOf(parameters);
    }

    private static ParameterDefinition defineParameter(int index, java.lang.reflect.Parameter param) {
        Parameter annotation = param.getAnnotation(Parameter.class);
        String name = annotation != null && !annotation.value().isBlank()
                      ? annotation.value()
                      : fallbackName(param);
        String description = annotation != null ? annotation.description() : "";
        boolean required = annotation == null || annotation.required();

        return new ParameterDefinition(index, normalise(name), description, required, param.getType());
    }

    private static List<Class<? extends Condition<?>>> conditions(Class<?> owner, Method method) {
        Guard guard = method.getAnnotation(Guard.class);
        if (guard == null) guard = owner.getAnnotation(Guard.class);
        return guard == null ? List.of() : List.of(guard.value());
    }

    private static Segment segment(Command command, AnnotatedElement element) {
        if (command.value().isBlank()) throw new IllegalArgumentException("Command " + element + " has a blank name");
        return new Segment(command.value(), command.description());
    }

    private static List<Segment> append(List<Segment> path, Segment segment) {
        List<Segment> extended = new ArrayList<>(path);
        extended.add(segment);
        return List.copyOf(extended);
    }

    // Without -parameters, javac does not record parameter names, so the type name is used instead.
    private static String fallbackName(java.lang.reflect.Parameter param) {
        return param.isNamePresent() ? param.getName() : param.getType().getSimpleName();
    }

    private static String normalise(String name) {
        return name.toLowerCase(Locale.ROOT).replace(' ', '_');
    }

}
