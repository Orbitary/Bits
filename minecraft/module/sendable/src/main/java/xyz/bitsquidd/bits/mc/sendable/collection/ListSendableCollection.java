/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * An ordered list of sendables. New sendables added based on priority. Higher priority sendables are before lower priority ones. Sendables with the same priority are ordered by insertion time.
 */
public abstract non-sealed class ListSendableCollection<S extends Sendable> extends SendableCollection<S> {
    protected final List<SendableHandle<? extends S>> sendables = new ArrayList<>();

    protected ListSendableCollection() {}


    @Override
    public String toString() {
        return "ListSC{" + sendables + '}';
    }


    @ApiStatus.Internal
    @Override
    public final void mergeInto(SendableCollection<S> other, Receiver receiver) {
        if (!(other instanceof ListSendableCollection<S> otherList)) throw new UnsupportedOperationException("Cannot merge ListSendableCollection into non-ListSendableCollection");

        sendables.forEach(handle -> otherList.sendables.add(handle.cloneWith(receiver)));
    }

    @Override
    public void clear() {
        super.clear();
        sendables.clear();
    }

    @Override
    public final @Unmodifiable Set<SendableHandle<? extends S>> getAll() {
        return Set.copyOf(sendables);
    }

    @Override
    protected final void removeInternal(SendableHandle<? extends S> handle) {
        sendables.remove(handle);
    }


    public final <SE extends S> Optional<SendableHandle<SE>> add(SE sendable) {
        int priority = sendable.config().priority();
        int index = 0;

        List.copyOf(sendables).stream().filter(handle -> sendable.config().replaces(handle.definition())).forEach(h -> this.remove(h::equals));
        while (index < sendables.size() && sendables.get(index).config().priority() >= priority) {
            index++;
        }

        SendableHandle<SE> handle = createHandle(sendable);
        sendables.add(index, handle);
        return Optional.of(handle);
    }

}