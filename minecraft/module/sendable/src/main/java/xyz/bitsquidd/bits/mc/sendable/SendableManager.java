/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import org.jetbrains.annotations.ApiStatus;

import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableStorage;
import xyz.bitsquidd.bits.mc.sendable.collection.WeakStorage;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.util.Safety;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public abstract class SendableManager<S extends Sendable> implements CoreManager {
    protected final ConcurrentHashMap<Receiver, SendableStorage<S>> playerSendables = new ConcurrentHashMap<>();

    protected SendableManager() {}


    public final void tickAll() {
        playerSendables.forEach((r, c) -> Safety.safeExecute(
          c.getClass().getSimpleName(),
          () -> {
              c.tick();

              SendableStorage<S> global = getOrCreateCollection(GlobalReceiver.INSTANCE);
              boolean needsRender = c.needsRender() || global.needsRender();

              if (needsRender) {
                  render(r, WeakStorage.from(List.of(c, global)));
                  c.markRendered();
              }
          }
        ));
    }

    protected abstract void render(Receiver receiver, WeakStorage<? extends S> storage);


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
    public final SendableStorage<S> getOrCreateCollection(Receiver receiver) {
        return playerSendables.computeIfAbsent(
          receiver,
          r -> new SendableStorage<>()
        );
    }


    //region Operations
    @ApiStatus.Internal
    public final void put(Receiver receiver, SendableHandle<? extends S> handle) {
        getOrCreateCollection(receiver).put(handle);
    }

    @ApiStatus.Internal
    public final List<SendableHandle<? extends S>> get(Receiver receiver, SendableFilter<? super S> filter) {
        var collection = playerSendables.get(receiver);
        if (collection == null) return Collections.emptyList();
        return collection.get(filter);
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public final <SE extends S> List<SendableHandle<SE>> get(Receiver receiver, Class<? extends SE> clazz) {
        return get(receiver, SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<SE>)handle)
          .collect(Collectors.toList());
    }

    @ApiStatus.Internal
    public final void remove(Receiver receiver, SendableFilter<? super S> filter) {
        var collection = playerSendables.get(receiver);
        if (collection != null) collection.remove(filter);
    }


    // Note: Syntactically different from GlobalReceiver.INSTANCE.put() as this will not trigger for non-initialized receivers at the time of call.
    public final void putAll(Receiver receiver, List<? extends SendableHandle<S>> sendables) {
        sendables.forEach(sendable -> put(receiver, sendable));
    }

    public final List<SendableHandle<? extends S>> getAll(SendableFilter<? super S> filter) {
        return playerSendables.values().stream().flatMap(c -> c.get(filter).stream()).collect(Collectors.toList());
    }

    public final void removeAll(SendableFilter<? super S> filter) {
        playerSendables.values().forEach(c -> c.remove(filter));
    }
    //endregion


    @Override
    public void cleanup() {
        removeAll(SendableFilter.alwaysTrue());
        playerSendables.clear();
    }

}
