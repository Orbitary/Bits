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
import xyz.bitsquidd.bits.mc.sendable.collection.WeakStorage;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;
import xyz.bitsquidd.bits.mc.sendable.impl.title.TitleManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PaperTitleManager extends TitleManager {
    private final Map<UUID, Boolean> hasActiveTitle = new ConcurrentHashMap<>();

    @Override
    protected void render(Receiver receiver, WeakStorage<? extends AbstractTitle> storage) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        UUID uuid = receiver.getUniqueId();
        List<? extends SendableHandle<? extends AbstractTitle>> titles = storage.getAll();
        if (titles.isEmpty()) {
            if (Boolean.TRUE.equals(hasActiveTitle.put(uuid, false))) {
                paperReceiver.sendPacket(new ClientboundSetTitlesAnimationPacket(0, 0, 0));
                paperReceiver.sendPacket(new ClientboundSetSubtitleTextPacket(Component.empty()));
                paperReceiver.sendPacket(new ClientboundSetTitleTextPacket(Component.empty()));
            }
            return;
        }
        hasActiveTitle.put(uuid, true);
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

    @Override
    protected void shutdownReceiver(Receiver receiver) {
        super.shutdownReceiver(receiver);
        hasActiveTitle.remove(receiver.getUniqueId());
    }

}
