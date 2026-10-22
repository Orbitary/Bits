/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Reads {@link CommandDefinition}s from the {@link Command} methods of a class.
 * <p>
 * Every annotated method must be public. Argument names are normalised to lower case with spaces
 * replaced by underscores. Required arguments may not follow optional ones.
 *
 * @since 0.1.6
 */
public final class CommandScanner {

    private CommandScanner() {}

    /**
     * Reads every {@link Command} method declared by {@code owner}.
     *
     * @param owner the class to scan
     *
     * @return the commands declared by {@code owner}, or an empty list if there are none
     *
     * @throws IllegalArgumentException if a command is invalid or its name is already used by another command in {@code owner}
     *
     * @since 0.1.6
     */
    public static List<CommandDefinition> scan(Class<?> owner) {
        List<CommandDefinition> commands = new ArrayList<>();
        Set<String> names = new HashSet<>();

        for (Method method : owner.getDeclaredMethods()) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null) continue;

            CommandDefinition command = define(owner, method, annotation);
            if (!names.add(command.name())) {
                throw new IllegalArgumentException("Duplicate command '" + command.name() + "' in " + owner.getName());
            }
            commands.add(command);
        }
        return commands;
    }

    private static CommandDefinition define(Class<?> owner, Method method, Command annotation) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("Command method " + method + " must be public");
        }
        if (annotation.value().isBlank()) {
            throw new IllegalArgumentException("Command method " + method + " has a blank name");
        }

        List<ParameterDefinition> parameters = new ArrayList<>();
        Set<String> parameterNames = new HashSet<>();
        boolean optionalSeen = false;

        for (java.lang.reflect.Parameter param : method.getParameters()) {
            ParameterDefinition parameter = defineParameter(param);
            if (!parameterNames.add(parameter.name())) {
                throw new IllegalArgumentException("Duplicate argument '" + parameter.name() + "' in " + method);
            }
            if (!parameter.required()) {
                optionalSeen = true;
            } else if (optionalSeen) {
                throw new IllegalArgumentException("Required argument '" + parameter.name() + "' follows an optional argument in " + method);
            }
            parameters.add(parameter);
        }

        return new CommandDefinition(annotation.value(), annotation.description(), owner, method, List.copyOf(parameters));
    }

    private static ParameterDefinition defineParameter(java.lang.reflect.Parameter param) {
        Parameter annotation = param.getAnnotation(Parameter.class);
        String name = annotation != null && !annotation.value().isBlank()
                      ? annotation.value()
                      : fallbackName(param);
        String description = annotation != null ? annotation.description() : "";
        boolean required = annotation == null || annotation.required();

        return new ParameterDefinition(normalise(name), description, required, param.getType());
    }

    // Without -parameters, javac does not record parameter names, so the type name is used instead.
    private static String fallbackName(java.lang.reflect.Parameter param) {
        return param.isNamePresent() ? param.getName() : param.getType().getSimpleName();
    }

    private static String normalise(String name) {
        return name.toLowerCase(Locale.ROOT).replace(' ', '_');
    }

}
