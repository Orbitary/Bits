/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable.tablist;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;

import xyz.orbitary.bits.mc.sendable.PaperReceiver;
import xyz.orbitary.bits.mc.sendable.Receiver;
import xyz.orbitary.bits.mc.sendable.SendableFilter;
import xyz.orbitary.bits.mc.sendable.collection.WeakStorage;
import xyz.orbitary.bits.mc.sendable.impl.SendableHandle;
import xyz.orbitary.bits.mc.sendable.impl.SendableState;
import xyz.orbitary.bits.mc.sendable.impl.tablist.AbstractTablist;
import xyz.orbitary.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.orbitary.bits.mc.sendable.impl.tablist.data.TablistPosition;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PaperTablistManager extends TablistManager {
    private final Map<UUID, Component> lastHeader = new ConcurrentHashMap<>();
    private final Map<UUID, Component> lastFooter = new ConcurrentHashMap<>();

    @Override
    protected void render(Receiver receiver, WeakStorage<? extends AbstractTablist> storage) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        Map<TablistPosition, Component> positionContentMap = new EnumMap<>(TablistPosition.class);

        for (TablistPosition value : TablistPosition.values()) {
            Component content = Component.empty();

            SendableHandle<? extends AbstractTablist> handle = storage.getFirst(SendableFilter.withData(TABLIST_INDEX, value)).orElse(null);
            if (handle != null) {
                SendableState state = handle.state(receiver);
                content = handle.definition().content(state);
            }

            positionContentMap.put(value, content);
        }

        UUID uuid = receiver.getUniqueId();
        Component header = positionContentMap.get(TablistPosition.HEADER);
        Component footer = positionContentMap.get(TablistPosition.FOOTER);

        if (Objects.equals(header, lastHeader.get(uuid)) && Objects.equals(footer, lastFooter.get(uuid))) return;

        lastHeader.put(uuid, header);
        lastFooter.put(uuid, footer);

        paperReceiver.sendPackets(new ClientboundTabListPacket(
          PaperAdventure.asVanillaNullToEmpty(header),
          PaperAdventure.asVanillaNullToEmpty(footer)
        ));
    }

    @Override
    protected void shutdownReceiver(Receiver receiver) {
        super.shutdownReceiver(receiver);
        lastHeader.remove(receiver.getUniqueId());
        lastFooter.remove(receiver.getUniqueId());
    }

}
