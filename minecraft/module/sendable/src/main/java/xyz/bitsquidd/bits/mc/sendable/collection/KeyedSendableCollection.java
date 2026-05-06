/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableFilter;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.List;
import java.util.Map;


public abstract class KeyedSendableCollection<K, S extends Sendable> extends SendableCollection<S> {
    protected final BiMap<K, SendableHandle<S>> sendables = HashBiMap.create();

    protected KeyedSendableCollection(Receiver receiver) {
        super(receiver);
    }


    @Override
    public String toString() {
        return "KeyedSC{" + sendables + '}';
    }


    @Unmodifiable
    @Override
    public final List<SendableHandle<S>> getAll() {
        return sendables.values().stream().toList();
    }

    @Override
    protected final void removeInternal(SendableFilter<S> filter) {
        sendables.values().removeIf(filter);
    }


    public void add(K key, S sendable) {
        this.sendables.put(key, createHandle(sendable));
    }

    public void add(Map<K, ? extends S> sendables) {
        sendables.forEach(this::add);
    }

}