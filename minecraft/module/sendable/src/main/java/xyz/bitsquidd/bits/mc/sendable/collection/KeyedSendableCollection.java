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
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;


/**
 * A collection of sendables that can be accessed by a key.
 *
 * @param <K>
 * @param <S>
 */
public abstract class KeyedSendableCollection<K, S extends Sendable> extends SendableCollection<S> {
    protected final BiMap<K, SendableHandle<S>> sendables = HashBiMap.create();

    protected KeyedSendableCollection() {}

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
    protected final void removeInternal(SendableHandle<? super S> filter) {
        sendables.values().remove(filter);
    }


    public final void add(K key, S sendable, Receiver receiver) {
        if (this.sendables.containsKey(key)) {
            SendableHandle<S> existingHandle = this.sendables.get(key);
            if (sendable.config().priority() < existingHandle.config().priority() && !sendable.config().replaces(existingHandle.definition())) return; // Existing sendable has higher priority, do not replace

            remove(h -> h.equals(existingHandle)); // Expire the existing sendable before replacing
        }

        sendables.put(key, createHandle(sendable, receiver));
    }

    public Optional<SendableHandle<S>> get(K key) {
        return Optional.ofNullable(sendables.get(key));
    }

    public void forEach(BiConsumer<? super K, ? super SendableHandle<S>> action) {
        sendables.forEach(action);
    }


}