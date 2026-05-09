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
import xyz.bitsquidd.bits.wrapper.collection.Single;

import java.util.Optional;
import java.util.Set;


public abstract non-sealed class SingleSendableCollection<S extends Sendable> extends SendableCollection<S> {
    protected final Single<SendableHandle<? extends S>> sendables = new Single<>();

    protected SingleSendableCollection() {}


    @Override
    public String toString() {
        return "SingleSC{" + sendables + '}';
    }


    @ApiStatus.Internal
    @Override
    public final void mergeInto(SendableCollection<S> other, Receiver receiver) {
        if (!(other instanceof SingleSendableCollection<S> otherSingle)) throw new UnsupportedOperationException("Cannot merge SingleSendableCollection into non-SingleSendableCollection");

        this.sendables.get().ifPresent(handle -> otherSingle.sendables.set(handle.cloneWith(receiver)));
    }

    @Override
    public void clear() {
        super.clear();
        sendables.set(null);
    }

    @Override
    public final @Unmodifiable Set<SendableHandle<? extends S>> getAll() {
        return sendables.asSet();
    }

    @Override
    protected final void removeInternal(SendableHandle<? extends S> handle) {
        sendables.removeIf(h -> h.equals(handle));
    }


    public final <SE extends S> Optional<SendableHandle<SE>> add(SE sendable) {
        SendableHandle<? extends S> existingHandle = this.sendables.get().orElse(null);
        if (existingHandle != null) {
            if (sendable.config().priority() < existingHandle.config().priority() && !sendable.config().replaces(existingHandle.definition())) return Optional.empty(); // Existing sendable has higher priority, do not replace

            remove(h -> h.equals(existingHandle)); // Expire the existing sendable before replacing
        }

        SendableHandle<SE> handle = createHandle(sendable);
        this.sendables.set(handle);
        return Optional.of(handle);
    }

}