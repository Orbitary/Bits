/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable.actionbar;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;

import xyz.orbitary.bits.mc.sendable.PaperReceiver;
import xyz.orbitary.bits.mc.sendable.Receiver;
import xyz.orbitary.bits.mc.sendable.collection.WeakStorage;
import xyz.orbitary.bits.mc.sendable.impl.SendableState;
import xyz.orbitary.bits.mc.sendable.impl.actionbar.AbstractActionbar;
import xyz.orbitary.bits.mc.sendable.impl.actionbar.ActionbarManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PaperActionbarManager extends ActionbarManager {
    private final Map<UUID, Component> lastContent = new ConcurrentHashMap<>();

    @Override
    protected void render(Receiver receiver, WeakStorage<? extends AbstractActionbar> storage) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        ComponentBuilder<?, ?> builder = Component.text();
        boolean keepalive = false;

        for (var actionbarHandle : storage.getAll()) {
            SendableState state = actionbarHandle.state(receiver);
            merge(builder, actionbarHandle.definition().content(state));

            long tick = state.tick();
            if (tick >= 0 && tick % actionbarHandle.definition().config().keepaliveTicks == 0) keepalive = true;
        }

        Component content = builder.build();
        UUID uuid = receiver.getUniqueId();

        Component last = lastContent.put(uuid, content);

        if (!keepalive && content.equals(last)) return;

        paperReceiver.sendPackets(new ClientboundSetActionBarTextPacket(
          PaperAdventure.asVanillaNullToEmpty(content)
        ));
    }

    @Override
    protected void shutdownReceiver(Receiver receiver) {
        super.shutdownReceiver(receiver);
        lastContent.remove(receiver.getUniqueId());
    }

    /**
     * Merges the given actionbar. A more refined implementation may want to ZEROWIDTH the new content to have more control over spacing and ordering.
     */
    protected void merge(ComponentBuilder<?, ?> builder, Component component) {
        builder.append(component);
    }

}
