/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.util.attribute;

import net.kyori.adventure.key.Key;


public record SerializedAttribute(
  Key attributeId,
  double value,
  Operation operation
) {
    public enum Operation {ADD_NUMBER, ADD_SCALAR, MULTIPLY_SCALAR_1}

}
