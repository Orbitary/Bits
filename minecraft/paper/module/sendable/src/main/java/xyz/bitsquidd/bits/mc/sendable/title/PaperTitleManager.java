/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.title;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.title.Title;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;

import xyz.bitsquidd.bits.format.Time;
import xyz.bitsquidd.bits.mc.sendable.PaperReceiver;
import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.collection.SendableCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;
import xyz.bitsquidd.bits.mc.sendable.impl.title.TitleManager;

import java.util.List;


public class PaperTitleManager extends TitleManager {

    @Override
    protected void render(Receiver receiver, SendableCollection.Multiple<AbstractTitle> collection) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        List<SendableHandle<? extends AbstractTitle>> titles = collection.getAll();
        if (titles.isEmpty()) {
            paperReceiver.sendPacket(new ClientboundSetTitlesAnimationPacket(0, 0, 0));
            paperReceiver.sendPacket(new ClientboundSetSubtitleTextPacket(Component.empty()));
            paperReceiver.sendPacket(new ClientboundSetTitleTextPacket(Component.empty()));
            return;
        }
        SendableHandle<? extends AbstractTitle> handle = titles.getFirst();

        SendableState state = handle.state(receiver);

        Title.Times times = handle.definition().getTimes(state);
        paperReceiver.sendPacket(new ClientboundSetTitlesAnimationPacket(
          Time.TO_TICKS(times.fadeIn()),
          Time.TO_TICKS(times.stay()),
          Time.TO_TICKS(times.fadeOut())
        ));

        paperReceiver.sendPacket(new ClientboundSetSubtitleTextPacket(
          PaperAdventure.asVanillaNullToEmpty(handle.definition().subtitle(state))
        ));

        paperReceiver.sendPacket(new ClientboundSetTitleTextPacket(
          PaperAdventure.asVanillaNullToEmpty(handle.definition().title(state))
        ));
    }

}
