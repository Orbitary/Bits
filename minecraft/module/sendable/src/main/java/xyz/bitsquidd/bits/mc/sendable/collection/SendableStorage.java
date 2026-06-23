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
import java.util.concurrent.CopyOnWriteArrayList;


public final class SendableStorage<S extends Sendable> implements Storage<S> {
    private final List<SendableHandle<? extends S>> sendables = new CopyOnWriteArrayList<>();

    private boolean needsForceRender = false;


    //region Collection Operations
    public synchronized void put(SendableHandle<? extends S> sendable) {
        SendableFilter<Sendable> replaces = sendable.config().replaces();
        if (replaces != null) remove(replaces);

        int priority = sendable.config().priority();
        int index = 0;
        while (index < sendables.size() && sendables.get(index).config().priority() >= priority) {
            index++;
        }
        sendables.add(index, sendable);
    }

    @Override
    public @Unmodifiable List<SendableHandle<? extends S>> getAll() {
        return List.copyOf(sendables);
    }

    @Override
    public @Unmodifiable List<SendableHandle<? extends S>> get(SendableFilter<? super S> filter) {
        return sendables.stream().filter(filter).toList();
    }

    @Override
    public Optional<SendableHandle<? extends S>> getFirst(SendableFilter<? super S> filter) {
        return sendables.stream().filter(filter).findFirst();
    }

    public boolean remove(SendableFilter<? super S> filter) {
        return sendables.removeIf(handle -> {
            if (!filter.test(handle)) return false;

            if (!handle.isExpired()) handle.bits$markForExpire();
            needsForceRender = true; // We mark a final render on remove, only when something is removed.
            return true;
        });
    }
    //endregion


    //region Lifecycle
    public void tick() {
        sendables.forEach(SendableHandle::bits$tick);
        remove(SendableHandle::isExpired);
    }

    public boolean needsRender() {
        return sendables.stream().anyMatch(SendableHandle::needsRender) || needsForceRender;
    }

    public void markRendered() {
        sendables.forEach(SendableHandle::bits$markRendered);
        needsForceRender = false;
    }
    //endregion

}
