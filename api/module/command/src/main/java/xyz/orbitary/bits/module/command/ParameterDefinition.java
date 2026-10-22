/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

/**
 * A command argument read from a {@link Command} method parameter.
 *
 * @param index       the position of the parameter in the method signature
 * @param name        the normalised argument name
 * @param description the description, or empty for none
 * @param required    whether the argument must be supplied
 * @param type        the Java type the argument is converted to
 *
 * @since 0.1.6
 */
public record ParameterDefinition(int index, String name, String description, boolean required, Class<?> type) {}
