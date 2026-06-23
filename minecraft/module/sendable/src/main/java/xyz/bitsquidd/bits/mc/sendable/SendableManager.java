/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import org.jetbrains.annotations.ApiStatus;

import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.mc.sendable.collection.OperationSuite;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.util.Safety;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public abstract class SendableManager<S extends Sendable, O extends OperationSuite<S>, C extends SendableCollection<S, O>> implements CoreManager {
    private final Supplier<C> collectionSupplier;
    protected final ConcurrentHashMap<Receiver, C> playerSendables = new ConcurrentHashMap<>();

    protected SendableManager(Supplier<C> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }


    public final void tickAll() {
        playerSendables.forEach((r, c) -> Safety.safeExecute(
          c.getClass().getSimpleName(),
          () -> {
              c.tick();
              if (c.needsRender()) {
                  render(r, c);
                  c.markRendered();
              }
          }
        ));
    }

    protected abstract void render(Receiver receiver, C collection);


    /**
     * Initialises the receiver's collection. Should only be called ONCE on join.
     */
    protected void startupReceiver(Receiver receiver) {
        shutdownReceiver(receiver);
        getOrCreateCollection(receiver);
    }

    /**
     * Cleans up the receiver's collection. Should only be called ONCE on quit.
     */
    protected void shutdownReceiver(Receiver receiver) {
        playerSendables.remove(receiver);
    }


    //region Handle Callbacks
    @ApiStatus.Internal
    public void onAdd(Receiver receiver, SendableHandle<? extends S> handle) {
        // Default implementation does nothing.
    }

    @ApiStatus.Internal
    public void onTick(Receiver receiver, SendableHandle<? extends S> handle) {
        // Default implementation does nothing.
    }

    @ApiStatus.Internal
    public void onExpire(Receiver receiver, SendableHandle<? extends S> handle) {
        // Default implementation does nothing.
    }
    //endregion


    @ApiStatus.Internal
    public final C getOrCreateCollection(Receiver receiver) {
        return playerSendables.computeIfAbsent(
          receiver,
          r -> collectionSupplier.get()
        );
    }


    //region Operations
    public final Optional<SendableHandle<S>> put(Receiver receiver, O operation) {
        return getOrCreateCollection(receiver).put(operation);
    }

    public final void putAll(O operation) {
        playerSendables.values().forEach(c -> c.put(operation));
    }


    public final List<SendableHandle<S>> get(Receiver receiver, SendableFilter<? super S> filter) {
        var collection = playerSendables.get(receiver);
        if (collection == null) return Collections.emptyList();
        return collection.get(filter);
    }

    @SuppressWarnings("unchecked")
    public final <SE extends S> List<SendableHandle<SE>> get(Receiver receiver, Class<? extends SE> clazz) {
        return get(receiver, SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<SE>)handle)
          .collect(Collectors.toList());
    }


    public final List<SendableHandle<S>> getAll(SendableFilter<? super S> filter) {
        return playerSendables.values().stream().flatMap(c -> c.get(filter).stream()).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public final <SE extends S> List<SendableHandle<SE>> getAll(Class<? extends SE> clazz) {
        return getAll(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<SE>)handle)
          .collect(Collectors.toList());
    }


    public final void remove(Receiver receiver, SendableFilter<? super S> filter) {
        var collection = playerSendables.get(receiver);
        if (collection != null) collection.remove(filter);
    }

    public final void remove(Receiver receiver, Class<? extends S> clazz) {
        remove(receiver, SendableFilter.ofClass(clazz));
    }

    public final void removeAll(SendableFilter<? super S> filter) {
        playerSendables.values().forEach(c -> c.remove(filter));
    }

    public final void removeAll(Class<? extends S> clazz) {
        removeAll(SendableFilter.ofClass(clazz));
    }

    //endregion


    @Override
    public void cleanup() {
        removeAll(SendableFilter.alwaysTrue());
        playerSendables.clear();
    }

}
