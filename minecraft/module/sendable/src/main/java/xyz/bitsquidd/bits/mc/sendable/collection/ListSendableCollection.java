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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * An ordered list of sendables. New sendables added based on priority. Higher priority sendables are before lower priority ones. Sendables with the same priority are ordered by insertion time.
 */
public abstract class ListSendableCollection<S extends Sendable> extends SendableCollection<S> {
    protected final List<SendableHandle<S>> sendables = new ArrayList<>();

    protected ListSendableCollection(Receiver receiver) {
        super(receiver);
    }


    @Override
    public String toString() {
        return "ListSC{" + sendables + '}';
    }


    @Unmodifiable
    @Override
    public final List<SendableHandle<S>> getAll() {
        return List.copyOf(sendables);
    }

    @Override
    protected final void removeInternal(SendableHandle<S> handle) {
        sendables.remove(handle);
    }


    public void add(S sendable) {
        int priority = sendable.config().priority();
        int index = 0;
        while (index < sendables.size() && sendables.get(index).definition.config().priority() >= priority) {
            index++;
        }
        this.sendables.add(index, createHandle(sendable));
    }

    public void add(Collection<? extends S> sendables) {
        sendables.forEach(this::add);
    }

}