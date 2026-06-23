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

import xyz.bitsquidd.bits.mc.sendable.SendableFilter;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.wrapper.collection.Single;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


public sealed abstract class SendableCollection<S extends Sendable, P extends OperationSuite<S>> permits SendableCollection.Keyed, SendableCollection.Multiple, SendableCollection.Replace {
    private boolean needsForceRender = false;


    //region Collection Operations
    public abstract Optional<SendableHandle<S>> put(P operation);

    public abstract @Unmodifiable List<SendableHandle<? extends S>> getAll();

    protected abstract void removeInternal(SendableHandle<? extends S> handle);


    @SuppressWarnings("unchecked") // We cast to <S> for simplicity - especially for public-facing methods.
    public final @Unmodifiable List<SendableHandle<S>> get(SendableFilter<? super S> filter) {
        return (List<SendableHandle<S>>)(List<?>)getAll().stream().filter(filter).toList();
    }

    public final void remove(SendableFilter<? super S> filter) {
        get(filter).forEach(handle -> {
            if (!handle.isExpired()) handle.bits$markForExpire();
            removeInternal(handle);
            needsForceRender = true; // We mark a final render on remove, only when something is removed.
        });
    }
    //endregion


    //region Lifecycle
    public final void tick() {
        getAll().forEach(SendableHandle::bits$tick);
        remove(SendableHandle::isExpired);
    }

    public final boolean needsRender() {
        return getAll().stream().anyMatch(SendableHandle::needsRender) || needsForceRender;
    }

    public final void markRendered() {
        getAll().forEach(SendableHandle::bits$markRendered);
        needsForceRender = false;
    }
    //endregion


    public static final class Multiple<S extends Sendable> extends SendableCollection<S, OperationSuite.Multiple<S>> {
        private final List<SendableHandle<? extends S>> sendables = new CopyOnWriteArrayList<>();

        @Override
        public Optional<SendableHandle<S>> put(OperationSuite.Multiple<S> operation) {
            SendableHandle<S> handle = operation.sendable();
            S definition = handle.definition();

            int priority = definition.config().priority();
            int index = 0;

            List.copyOf(sendables).stream().filter(h -> definition.config().replaces(handle.definition())).forEach(h -> this.remove(h::equals));
            while (index < sendables.size() && sendables.get(index).config().priority() >= priority) {
                index++;
            }

            sendables.add(index, handle);
            return Optional.of(handle);
        }

        @Override
        public @Unmodifiable List<SendableHandle<? extends S>> getAll() {
            return List.copyOf(sendables);
        }

        @Override
        protected void removeInternal(SendableHandle<? extends S> handle) {
            sendables.remove(handle);
        }

    }


    public static final class Replace<S extends Sendable> extends SendableCollection<S, OperationSuite.Replace<S>> {
        private final Single<SendableHandle<? extends S>> sendables = new Single<>();

        @Override
        public Optional<SendableHandle<S>> put(OperationSuite.Replace<S> operation) {
            SendableHandle<S> handle = operation.sendable();
            S definition = handle.definition();

            SendableHandle<? extends S> existingHandle = sendables.get().orElse(null);
            if (existingHandle != null) {
                if (definition.config().priority() < existingHandle.config().priority() && !definition.config().replaces(existingHandle.definition())) return Optional.empty(); // Existing sendable has higher priority, do not replace

                remove(h -> h.equals(existingHandle)); // Expire the existing sendable before replacing
            }

            this.sendables.set(handle);
            return Optional.of(handle);
        }


        @Override
        public @Unmodifiable List<SendableHandle<? extends S>> getAll() {
            return sendables.asList();
        }

        @Override
        protected void removeInternal(SendableHandle<? extends S> handle) {
            sendables.removeIf(h -> h.equals(handle));
        }

    }


    public static final class Keyed<K, S extends Sendable> extends SendableCollection<S, OperationSuite.Keyed<K, S>> {
        private final BiMap<K, SendableHandle<? extends S>> sendables = HashBiMap.create();

        @Override
        public Optional<SendableHandle<S>> put(OperationSuite.Keyed<K, S> operation) {
            SendableHandle<S> handle = operation.sendable();
            S definition = handle.definition();
            K key = operation.key();

            if (sendables.containsKey(key)) {
                SendableHandle<? extends S> existingHandle = sendables.get(key);
                if (definition.config().priority() < existingHandle.config().priority() && !definition.config().replaces(existingHandle.definition())) return Optional.empty(); // Existing sendable has higher priority, do not replace

                remove(h -> h.equals(existingHandle)); // Expire the existing sendable before replacing
            }

            sendables.put(key, handle);
            return Optional.of(handle);
        }

        @SuppressWarnings("unchecked")
        public Optional<SendableHandle<S>> get(K key) {
            return Optional.ofNullable((SendableHandle<S>)sendables.get(key));
        }

        @Override
        public @Unmodifiable List<SendableHandle<? extends S>> getAll() {
            return List.copyOf(sendables.values());
        }


        @Override
        protected void removeInternal(SendableHandle<? extends S> filter) {
            sendables.values().remove(filter);
        }


    }

}
