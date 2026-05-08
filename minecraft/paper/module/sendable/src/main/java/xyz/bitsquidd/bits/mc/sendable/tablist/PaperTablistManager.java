/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.tablist;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;

import xyz.bitsquidd.bits.mc.sendable.PaperReceiver;
import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.AbstractTablist;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.TablistCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;

import java.util.EnumMap;
import java.util.Map;


public class PaperTablistManager extends TablistManager {

    @Override
    protected void render(Receiver receiver, TablistCollection collection) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        Map<TablistPosition, Component> positionContentMap = new EnumMap<>(TablistPosition.class);

        for (TablistPosition value : TablistPosition.values()) {
            Component content = Component.empty();

            SendableHandle<AbstractTablist> handle = collection.get(value).orElse(null);
            if (handle != null) {
                SendableState state = handle.state();
                content = handle.definition().content(state);
            }

            positionContentMap.put(value, content);
        }

        paperReceiver.sendPacket(new ClientboundTabListPacket(
          PaperAdventure.asVanillaNullToEmpty(positionContentMap.get(TablistPosition.HEADER)),
          PaperAdventure.asVanillaNullToEmpty(positionContentMap.get(TablistPosition.FOOTER))
        ));
    }

}
