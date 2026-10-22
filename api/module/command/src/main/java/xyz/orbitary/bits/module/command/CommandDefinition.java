/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A command read from a {@link Command} method, independent of any platform.
 *
 * @param path       the command's segments from the outermost group to the command itself
 * @param owner      the class declaring the command method
 * @param method     the command method
 * @param parameters the arguments, excluding injected parameters
 * @param async      whether the command runs off the main thread
 * @param conditions the conditions that must pass before the command runs
 *
 * @since 0.1.6
 */
public record CommandDefinition(
  List<Segment> path,
  Class<?> owner,
  Method method,
  List<ParameterDefinition> parameters,
  boolean async,
  List<Class<? extends Condition<?>>> conditions
) {

    /**
     * One name in a command path, with the description of that group or command.
     *
     * @param name        the name
     * @param description the description, or empty for none
     *
     * @since 0.1.6
     */
    public record Segment(String name, String description) {}

}
