/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.ApiStatus;
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
public abstract non-sealed class KeyedSendableCollection<K, S extends Sendable> extends SendableCollection<S> {
    protected final BiMap<K, SendableHandle<? extends S>> sendables = HashBiMap.create();

    protected KeyedSendableCollection() {}

    @Override
    public String toString() {
        return "KeyedSC{" + sendables + '}';
    }

    
    @ApiStatus.Internal
    @Override
    public final void mergeInto(SendableCollection<S> other, Receiver receiver) {
        if (!(other instanceof KeyedSendableCollection<?, ?>)) throw new UnsupportedOperationException("Cannot merge KeyedSendableCollection into non-KeyedSendableCollection");
        KeyedSendableCollection<K, S> otherKeyed = (KeyedSendableCollection<K, S>)other;

        sendables.forEach((k, v) -> otherKeyed.sendables.put(k, v.cloneWith(receiver)));
    }

    @Unmodifiable
    @Override
    public final List<SendableHandle<? extends S>> getAll() {
        return sendables.values().stream().toList();
    }

    @Override
    protected final void removeInternal(SendableHandle<? extends S> filter) {
        sendables.values().remove(filter);
    }


    public final <SE extends S> Optional<SendableHandle<SE>> add(K key, SE sendable) {
        if (this.sendables.containsKey(key)) {
            SendableHandle<? extends S> existingHandle = this.sendables.get(key);
            if (sendable.config().priority() < existingHandle.config().priority() && !sendable.config().replaces(existingHandle.definition())) return Optional.empty(); // Existing sendable has higher priority, do not replace

            remove(h -> h.equals(existingHandle)); // Expire the existing sendable before replacing
        }

        SendableHandle<SE> handle = createHandle(sendable);
        sendables.put(key, handle);
        return Optional.of(handle);
    }

    public final Optional<SendableHandle<? extends S>> get(K key) {
        return Optional.ofNullable(sendables.get(key));
    }

    public final void forEach(BiConsumer<? super K, ? super SendableHandle<? extends S>> action) {
        sendables.forEach(action);
    }


}