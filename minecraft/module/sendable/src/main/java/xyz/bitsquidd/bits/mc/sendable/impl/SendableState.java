/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.wrapper.Percentage;


/**
 * State of a {@link SendableHandle} at a specific tick. Passed to {@link Sendable} definitions when rendering.
 */
public record SendableState(
  Receiver receiver,
  long tick,
  Percentage progress,
  boolean reversing,
  SendableHandle<?> handle
) {}
