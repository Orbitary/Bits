/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.lifecycle.manager.CoreManager;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class SendableManager<S extends Sendable, C extends SendableCollection<S>> implements CoreManager {
    private final Map<Receiver, C> playerSendables = new ConcurrentHashMap<>();


    public final void tickAll() {
        playerSendables.forEach((r, c) -> {
            c.tick();
            if (c.needsRender()) {
                render(r, c);
                c.markRendered();
            }
        });
    }

    protected abstract void render(Receiver receiver, C collection);

    protected abstract C createCollection(Receiver receiver);


    protected void initialisePlayer(Receiver receiver) {
        cleanupPlayer(receiver);
        playerSendables.computeIfAbsent(receiver, k -> createCollection(receiver));
    }

    protected void cleanupPlayer(Receiver receiver) {
        playerSendables.remove(receiver);
    }

}
