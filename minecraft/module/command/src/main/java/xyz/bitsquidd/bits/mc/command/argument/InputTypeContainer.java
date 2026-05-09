/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument;

import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

/**
 * A record that associates a type signature with a human-readable name identifier.
 * <p>
 * Primarily utilised during parser unrolling to track specific argument bounds correctly.
 *
 * @param typeSignature the type signature of the expected input
 * @param typeName      a descriptive name representing the input's purpose
 *
 * @since 0.0.10
 */
public record InputTypeContainer(
  TypeSignature<?> typeSignature,
  String typeName
) {}