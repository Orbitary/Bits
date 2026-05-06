/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;

import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.wrapper.collection.Single;

import java.util.List;


public abstract class SingleSendableCollection<S extends Sendable> extends SendableCollection<S> {
    protected final Single<SendableHandle<S>> sendables = new Single<>();

    protected SingleSendableCollection(Receiver receiver) {
        super(receiver);
    }


    @Override
    public String toString() {
        return "SingleSC{" + sendables + '}';
    }


    @Unmodifiable
    @Override
    public final List<SendableHandle<S>> getAll() {
        return sendables.asList();
    }

    @Override
    protected final void removeInternal(SendableHandle<? super S> handle) {
        sendables.removeIf(h -> h.equals(handle));
    }


    public void add(S sendable) {
        int priority = sendable.config().priority();
        SendableHandle<S> existingHandle = this.sendables.get();
        if (existingHandle != null) {
            if (existingHandle.definition.config().priority() > priority) return; // Existing sendable has higher priority, do not replace

            remove(h -> h.equals(existingHandle)); // Expire the existing sendable before replacing
        }

        this.sendables.set(createHandle(sendable));
    }

}