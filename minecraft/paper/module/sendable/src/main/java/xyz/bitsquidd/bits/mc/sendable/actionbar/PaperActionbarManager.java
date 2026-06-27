/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.actionbar;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;

import xyz.bitsquidd.bits.mc.sendable.PaperReceiver;
import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.collection.WeakStorage;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.AbstractActionbar;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarManager;


public class PaperActionbarManager extends ActionbarManager {

    @Override
    protected void render(Receiver receiver, WeakStorage<? extends AbstractActionbar> storage) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        ComponentBuilder<?, ?> builder = Component.text();
        storage.getAll().forEach(actionbarHandle -> merge(builder, actionbarHandle.definition().content(actionbarHandle.state(receiver))));

        paperReceiver.sendPacket(new ClientboundSetActionBarTextPacket(
          PaperAdventure.asVanillaNullToEmpty(builder.build())
        ));
    }

    /**
     * Merges the given actionbar. A more refined implementation may want to ZEROWIDTH the new content to have more control over spacing and ordering.
     */
    protected void merge(ComponentBuilder<?, ?> builder, Component component) {
        builder.append(component);
    }

}
