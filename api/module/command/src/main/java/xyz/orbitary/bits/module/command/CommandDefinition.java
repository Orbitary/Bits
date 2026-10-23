/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A command read from a {@link Command} method, independent of any platform.
 *
 * @param path                 the command's segments from the outermost group to the command itself
 * @param owner                the class declaring the command method
 * @param method               the command method
 * @param parameters           the command's arguments, excluding injected parameters
 * @param async                whether the command runs off the main thread
 * @param conditions           the conditions that must pass before the command runs
 * @param permissions          the permission nodes the actor needs to run the command
 * @param constructor          the constructor of {@code owner}, which creates the instance that runs the command
 * @param constructorParameters the arguments the constructor needs, which the path supplies before the command
 *
 * @since 0.1.6
 */
public record CommandDefinition(
  List<Segment> path,
  Class<?> owner,
  Method method,
  List<ParameterDefinition> parameters,
  boolean async,
  List<Class<? extends Condition<?>>> conditions,
  PermissionRule permissions,
  Constructor<?> constructor,
  List<ParameterDefinition> constructorParameters
) {

    /**
     * One name in a command path, with the description of that group or command.
     *
     * @param name        the name
     * @param description the description, or empty for none
     * @param parameters  the arguments a command group needs before its subcommands, or empty for none
     * @param aliases     other names for this segment, or empty for none
     *
     * @since 0.1.6
     */
    public record Segment(String name, String description, List<ParameterDefinition> parameters, List<String> aliases) {}

    /**
     * @return the path joined by spaces, such as {@code admin ban}
     *
     * @since 0.1.6
     */
    public String fullName() {
        return path.stream().map(Segment::name).collect(Collectors.joining(" "));
    }

}
