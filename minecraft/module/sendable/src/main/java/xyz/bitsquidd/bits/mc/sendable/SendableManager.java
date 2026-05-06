/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public abstract class SendableManager<S extends Sendable> {
    private final Map<UUID, SendableCollection<S>> playerSendables = new ConcurrentHashMap<>();


    public final void tick() {
        playerSendables.forEach((uuid, c) -> {
            c.tick();
            if (c.needsRender()) {
                render(uuid, c);
                c.markRendered();
            }
        });

    }

    protected abstract void render(UUID uuid, SendableCollection<S> collection);

    protected abstract SendableCollection<S> createCollection(Receiver receiver);


    protected void initialisePlayer(Receiver receiver) {
        playerSendables.computeIfAbsent(receiver.uniqueId(), k -> createCollection(receiver));
    }

    protected void cleanupPlayer(Receiver receiver) {
        playerSendables.remove(receiver.uniqueId());
    }

}
