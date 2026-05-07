/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;

import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarSendableCollection;


public class PaperActionbarManager extends ActionbarManager {

    @Override
    protected void render(Receiver receiver, ActionbarSendableCollection collection) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        TextComponent.Builder builder = Component.text();
        collection.getAll().forEach(actionbarHandle -> merge(builder, actionbarHandle.definition().content(actionbarHandle.state())));

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

}
