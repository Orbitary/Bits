/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import org.jetbrains.annotations.ApiStatus;

import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.util.Safety;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public abstract class SendableManager<S extends Sendable, C extends SendableCollection<S>> implements CoreManager {
    protected final Map<Receiver, C> playerSendables = new ConcurrentHashMap<>();
    protected final C globalSendables = createCollection(); // Non-rendering collection: used to tick ghost sendables, ready to merge on initialisation.

    private final Object lock = new Object();


    public final void tickAll() {
        Safety.safeExecute(globalSendables::tick);

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

    protected abstract C createCollection();

    /**
     * Initialises the receiver's collection. Should only be called ONCE on join.
     */
    protected void initialiseReceiver(Receiver receiver) {
        cleanupReceiver(receiver);
        getOrCreateCollection(receiver);
    }

    /**
     * Cleans up the receiver's collection. Should only be called ONCE on quit.
     */
    protected void cleanupReceiver(Receiver receiver) {
        playerSendables.remove(receiver);
    }

    /**
     * Sent when we clear all player sendables. Mainly for safety when they no longer have a collection to tick.
     */
    protected void forceCleanupUser(Receiver receiver) {}


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

    @ApiStatus.Internal
    public C getOrCreateCollection(Receiver receiver) {
        synchronized (lock) {
            return playerSendables.computeIfAbsent(
              receiver,
              k -> {
                  C personalCollection = createCollection();
                  personalCollection.setReceiver(receiver);
                  globalSendables.mergeInto(personalCollection, receiver);
                  return personalCollection;
              }
            );
        }
    }

    protected final void performGlobalAdd(Runnable addToGlobal, Consumer<Receiver> addToReceiver) {
        synchronized (lock) {
            addToGlobal.run();
            List.copyOf(playerSendables.keySet()).forEach(addToReceiver);
        }
    }


    //region Operations
    public final Set<SendableHandle<S>> get(Receiver receiver, SendableFilter<? super S> filter) {
        return getOrCreateCollection(receiver).get(filter);
    }

    @SuppressWarnings("unchecked")
    public final <SE extends S> Set<SendableHandle<SE>> get(Receiver receiver, Class<? extends SE> clazz) {
        return get(receiver, SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<SE>)handle)
          .collect(Collectors.toSet());
    }

    public final Set<SendableHandle<S>> getAll(SendableFilter<? super S> filter) {
        Set<SendableHandle<S>> handles = globalSendables.get(filter);
        playerSendables.forEach((r, c) -> handles.addAll(c.get(filter)));
        return handles;
    }

    @SuppressWarnings("unchecked")
    public final <SE extends S> Set<SendableHandle<SE>> getAll(Class<? extends SE> clazz) {
        return getAll(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<SE>)handle)
          .collect(Collectors.toSet());
    }

    public final void remove(Receiver receiver, SendableFilter<? super S> filter) {
        getOrCreateCollection(receiver).remove(filter);
    }

    public final void removeAll(SendableFilter<? super S> filter) {
        synchronized (lock) {
            globalSendables.remove(filter);
            playerSendables.forEach((r, c) -> c.remove(filter));
        }
    }
    //endregion


    @Override
    public void cleanup() {
        synchronized (lock) {
            removeAll(SendableFilter.alwaysTrue());
            playerSendables.keySet().forEach(this::forceCleanupUser);
            playerSendables.clear();
        }
    }

}
