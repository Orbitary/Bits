/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;


import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;


// Currently only add. TODO: implement more operations e.g. remove, set.
public sealed interface OperationSuite<S extends Sendable> {

    SendableHandle<S> sendable();

    record Replace<S extends Sendable>(
      SendableHandle<S> sendable
    ) implements OperationSuite<S> {}

    record Multiple<S extends Sendable>(
      SendableHandle<S> sendable
    ) implements OperationSuite<S> {}

    record Keyed<K, S extends Sendable>(
      K key,
      SendableHandle<S> sendable
    ) implements OperationSuite<S> {}

}
