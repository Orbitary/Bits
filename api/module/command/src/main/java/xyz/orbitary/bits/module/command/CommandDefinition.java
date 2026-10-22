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
 * @param name        the command name
 * @param description the description, or empty for none
 * @param owner       the class declaring the command method
 * @param method      the command method
 * @param parameters  the arguments, in method declaration order
 *
 * @since 0.1.6
 */
public record CommandDefinition(
  String name,
  String description,
  Class<?> owner,
  Method method,
  List<ParameterDefinition> parameters
) {}
