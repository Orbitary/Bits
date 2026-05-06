/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;

import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableFilter;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
    protected final void removeInternal(SendableFilter<S> filter) {
        sendables.removeIf(filter);
    }


    public void add(S sendable) {
        this.sendables.add(createHandle(sendable));
    }

    public void add(Collection<? extends S> sendables) {
        sendables.forEach(this::add);
    }

}