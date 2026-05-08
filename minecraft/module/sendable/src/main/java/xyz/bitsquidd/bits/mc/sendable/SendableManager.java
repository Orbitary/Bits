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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class SendableManager<S extends Sendable, C extends SendableCollection<S>> implements CoreManager {
    protected final Map<Receiver, C> playerSendables = new ConcurrentHashMap<>();


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

    protected abstract C createCollection();

    protected void initialiseReceiver(Receiver receiver) {
        cleanupReceiver(receiver);
        playerSendables.computeIfAbsent(receiver, k -> createCollection());
    }

    protected void cleanupReceiver(Receiver receiver) {
        playerSendables.remove(receiver);
    }


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

    // Receivers only.
    @ApiStatus.Internal
    protected C getCollection(Receiver receiver) {
        return playerSendables.getOrDefault(receiver, createCollection());
    }

}
