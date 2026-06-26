/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.state;


public record EffectModifier(
  // 1-indexed
  int amplifier,

  // <0 for infinite
  int durationTicks
) {
}
