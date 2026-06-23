/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;

import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.mc.sendable.SendableFilter;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.List;
import java.util.Optional;


public interface Storage<S extends Sendable> {
    @Unmodifiable
    List<SendableHandle<? extends S>> getAll();

    @Unmodifiable
    List<SendableHandle<? extends S>> get(SendableFilter<? super S> filter);

    Optional<SendableHandle<? extends S>> getFirst(SendableFilter<? super S> filter);

}
