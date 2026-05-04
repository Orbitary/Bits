/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;


import xyz.bitsquidd.bits.mc.sendable.impl.AbstractSendable;

import java.util.function.Predicate;


@FunctionalInterface
public interface SendableFilter<S extends AbstractSendable> extends Predicate<S> {

}
