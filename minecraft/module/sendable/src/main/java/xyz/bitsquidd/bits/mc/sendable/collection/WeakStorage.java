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

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public final class WeakStorage<S extends Sendable> implements Storage<S> {
    private final List<SendableHandle<? extends S>> sendables;

    private WeakStorage(List<SendableHandle<? extends S>> sendables) {
        this.sendables = sendables;
    }

    public static <SE extends Sendable> WeakStorage<SE> from(Collection<? extends Storage<SE>> storages) {
        List<SendableHandle<? extends SE>> merged = storages.stream()
          .flatMap(storage -> storage.getAll().stream())
          .toList();
        return new WeakStorage<>(merged);
    }


    @Override
    public @Unmodifiable List<SendableHandle<? extends S>> getAll() {
        return List.copyOf(sendables);
    }

    @Override
    public @Unmodifiable List<SendableHandle<? extends S>> get(SendableFilter<? super S> filter) {
        return getAll().stream().filter(filter).toList();
    }

    @Override
    public Optional<SendableHandle<? extends S>> getFirst(SendableFilter<? super S> filter) {
        return getAll().stream().filter(filter).findFirst();
    }

}
