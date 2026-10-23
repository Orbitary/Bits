/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import xyz.orbitary.bits.module.command.CommandDefinition.Segment;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;
import xyz.orbitary.bits.util.reflection.ScannerFlags;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Predicate;


/**
 * Reads {@link CommandDefinition}s from the {@link Command} methods of a class and its nested {@link Command} classes.
 * <p>
 * Every command method must be public. A {@link Command} class is a group: its constructor arguments are required
 * arguments that come before its subcommands, and it must be static and declare exactly one constructor. Argument
 * names are normalised to lower case with spaces replaced by underscores, and must be unique along a command's path.
 * Required arguments may not follow optional ones.
 *
 * @since 0.1.6
 */
public final class CommandScanner {

    private CommandScanner() {}

    /**
     * Reads every command in the classpath, treating only the parameter types {@code injected} accepts as injected.
     * Commands nested in another command class are read by that class.
     *
     * @param injected returns true for parameter types the platform supplies itself
     *
     * @return the commands, or an empty list if there are none
     *
     * @throws IllegalArgumentException if a command is invalid or its path is already used
     *
     * @since 0.1.6
     */
    public static List<CommandDefinition> scanAll(Predicate<Class<?>> injected) {
        List<CommandDefinition> commands = commandRoots().stream()
          .flatMap(root -> scan(root, injected).stream())
          .toList();
        checkUniquePaths(commands, "the classpath");
        return commands;
    }

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
        collect(owner, List.of(), List.of(), injected, commands);

        checkUniquePaths(commands, owner.getName());
        return commands;
    }

    private static void checkUniquePaths(List<CommandDefinition> commands, String source) {
        Set<String> paths = new HashSet<>();
        for (CommandDefinition command : commands) {
            if (!paths.add(command.fullName())) {
                throw new IllegalArgumentException("Duplicate command '" + command.fullName() + "' in " + source);
            }
        }
    }

    // Classes annotated with @Command or declaring a @Command method, skipping classes nested in another one
    // because the outer class's scan already covers them.
    private static List<Class<?>> commandRoots() {
        ScannerFlags flags = ScannerFlags.DEFAULT.withInnerClasses();
        Set<Class<?>> candidates = new HashSet<>(ReflectionUtils.Scanner.tryGetAnnotatedClasses("*", Command.class, flags));
        for (Method method : ReflectionUtils.Scanner.tryGetAnnotatedMethods("*", Command.class, flags)) {
            candidates.add(method.getDeclaringClass());
        }
        return candidates.stream().filter(candidate -> !hasEnclosingCandidate(candidate, candidates)).toList();
    }

    private static boolean hasEnclosingCandidate(Class<?> type, Set<Class<?>> candidates) {
        for (Class<?> enclosing = type.getEnclosingClass(); enclosing != null; enclosing = enclosing.getEnclosingClass()) {
            if (candidates.contains(enclosing)) return true;
        }
        return false;
    }

    private static void collect(Class<?> owner, List<Segment> prefix, List<PermissionClause> inherited, Predicate<Class<?>> injected, List<CommandDefinition> out) {
        Command group = owner.getAnnotation(Command.class);
        List<Segment> base = group == null
                             ? prefix
                             : append(prefix, segment(group, owner, parameters(constructor(owner), injected, true)));

        List<PermissionClause> clauses = concat(inherited, permissionsOf(owner));
        for (Method method : owner.getDeclaredMethods()) {
            Command command = method.getAnnotation(Command.class);
            if (command != null) out.add(define(owner, method, command, base, clauses, injected));
        }
        for (Class<?> nested : owner.getDeclaredClasses()) {
            if (nested.isAnnotationPresent(Command.class)) collect(nested, base, clauses, injected, out);
        }
    }

    private static CommandDefinition define(Class<?> owner, Method method, Command command, List<Segment> base, List<PermissionClause> inherited, Predicate<Class<?>> injected) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("Command method " + method + " must be public");
        }

        List<Segment> path;
        if (command.value().isBlank()) {
            // A blank name is only valid as the handler of a command group.
            if (!owner.isAnnotationPresent(Command.class)) throw new IllegalArgumentException("Command method " + method + " has a blank name");
            path = base;
        } else {
            path = append(base, segment(command, method, List.of()));
        }

        Constructor<?> constructor = constructor(owner);
        List<ParameterDefinition> constructorParameters = parameters(constructor, injected, true);
        if (!owner.isAnnotationPresent(Command.class) && constructor.getParameterCount() > 0) {
            throw new IllegalArgumentException("Command class " + owner.getName() + " takes constructor arguments but is not a command group");
        }
        List<ParameterDefinition> parameters = parameters(method, injected, false);
        checkArgumentNames(base, parameters, method);

        String defaultNode = "bits.command." + path.stream()
          .map(segment -> normalise(segment.name()))
          .collect(Collectors.joining("."));

        return new CommandDefinition(
          path,
          owner,
          method,
          parameters,
          method.isAnnotationPresent(Async.class) || owner.isAnnotationPresent(Async.class),
          conditions(owner, method),
          new PermissionRule(defaultNode, concat(inherited, permissionsOf(method))),
          constructor,
          constructorParameters
        );
    }

    private static void checkArgumentNames(List<Segment> base, List<ParameterDefinition> parameters, Method method) {
        Set<String> names = new HashSet<>();
        for (Segment segment : base) {
            for (ParameterDefinition parameter : segment.parameters()) names.add(parameter.name());
        }
        for (ParameterDefinition parameter : parameters) {
            if (!names.add(parameter.name())) {
                throw new IllegalArgumentException("Duplicate argument '" + parameter.name() + "' in " + method);
            }
        }
    }

    private static Constructor<?> constructor(Class<?> owner) {
        if (owner.isMemberClass() && !Modifier.isStatic(owner.getModifiers())) {
            throw new IllegalArgumentException("Command class " + owner.getName() + " must be static");
        }
        Constructor<?>[] constructors = owner.getDeclaredConstructors();
        if (constructors.length != 1) {
            throw new IllegalArgumentException("Command class " + owner.getName() + " must declare exactly one constructor");
        }
        return constructors[0];
    }

    private static List<ParameterDefinition> parameters(Executable executable, Predicate<Class<?>> injected, boolean group) {
        List<ParameterDefinition> parameters = new ArrayList<>();
        Set<String> names = new HashSet<>();
        boolean optionalSeen = false;

        java.lang.reflect.Parameter[] params = executable.getParameters();
        for (int i = 0; i < params.length; i++) {
            if (injected.test(params[i].getType())) continue;

            ParameterDefinition parameter = defineParameter(i, params[i]);
            if (group && !parameter.required()) {
                throw new IllegalArgumentException("Group argument '" + parameter.name() + "' must be required in " + executable);
            }
            if (!names.add(parameter.name())) {
                throw new IllegalArgumentException("Duplicate argument '" + parameter.name() + "' in " + executable);
            }
            if (!parameter.required()) {
                optionalSeen = true;
            } else if (optionalSeen) {
                throw new IllegalArgumentException("Required argument '" + parameter.name() + "' follows an optional argument in " + executable);
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
        Suggest suggest = param.getAnnotation(Suggest.class);
        List<String> suggestions = suggest == null ? List.of() : List.of(suggest.value());

        return new ParameterDefinition(index, normalise(name), description, required, param.getParameterizedType(), suggestions);
    }

    private static List<PermissionClause> permissionsOf(AnnotatedElement element) {
        return Arrays.stream(element.getAnnotationsByType(Permission.class))
          .map(permission -> new PermissionClause(List.of(permission.value()), permission.type()))
          .toList();
    }

    private static <T> List<T> concat(List<T> first, List<T> second) {
        List<T> joined = new ArrayList<>(first);
        joined.addAll(second);
        return List.copyOf(joined);
    }

    private static List<Class<? extends Condition<?>>> conditions(Class<?> owner, Method method) {
        Guard guard = method.getAnnotation(Guard.class);
        if (guard == null) guard = owner.getAnnotation(Guard.class);
        return guard == null ? List.of() : List.of(guard.value());
    }

    private static Segment segment(Command command, AnnotatedElement element, List<ParameterDefinition> parameters) {
        if (command.value().isBlank()) throw new IllegalArgumentException("Command " + element + " has a blank name");
        for (String alias : command.aliases()) {
            if (alias.isBlank()) throw new IllegalArgumentException("Command " + element + " has a blank alias");
        }
        return new Segment(command.value(), command.description(), parameters, List.of(command.aliases()));
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
