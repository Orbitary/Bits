/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.actionbar;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;

import xyz.bitsquidd.bits.mc.sendable.PaperReceiver;
import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarManager;


public class PaperActionbarManager extends ActionbarManager {

    @Override
    protected void render(Receiver receiver, ActionbarCollection collection) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        TextComponent.Builder builder = Component.text();
        collection.getAll().forEach(actionbarHandle -> merge(builder, actionbarHandle.definition().content(actionbarHandle.state(receiver))));

        paperReceiver.sendPacket(new ClientboundSetActionBarTextPacket(
          PaperAdventure.asVanillaNullToEmpty(builder.build())
        ));
    }

    /**
     * Merges the given actionbar. A more refined implementation may want to ZEROWIDTH the new content to have more control over spacing and ordering.
     */
    protected void merge(TextComponent.Builder builder, Component component) {
        builder.append(component);
    }

    @Override
    protected void forceCleanupUser(Receiver receiver) {
        super.forceCleanupUser(receiver);
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        paperReceiver.sendPacket(new ClientboundSetActionBarTextPacket(
          net.minecraft.network.chat.Component.empty()
        ));
    }

}
