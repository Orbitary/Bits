/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable.bossbar;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.world.BossEvent;

import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.mc.sendable.PaperReceiver;
import xyz.orbitary.bits.mc.sendable.Receiver;
import xyz.orbitary.bits.mc.sendable.SendableFilter;
import xyz.orbitary.bits.mc.sendable.collection.WeakStorage;
import xyz.orbitary.bits.mc.sendable.impl.SendableHandle;
import xyz.orbitary.bits.mc.sendable.impl.SendableState;
import xyz.orbitary.bits.mc.sendable.impl.bossbar.AbstractBossbar;
import xyz.orbitary.bits.mc.sendable.impl.bossbar.BossbarManager;
import xyz.orbitary.bits.mc.sendable.impl.bossbar.data.BossBarColor;
import xyz.orbitary.bits.mc.sendable.impl.bossbar.data.BossBarOverlay;
import xyz.orbitary.bits.wrapper.Percentage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class PaperBossbarManager extends BossbarManager {
    private static final int MAX_BOSSBARS = 10; // Arbitrary limit for allowed bossbars

    private final ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, BossEvent>> bossbarIds = new ConcurrentHashMap<>();
    private final Map<UUID, boolean[]> blankSlots = new ConcurrentHashMap<>();

    @Override
    protected void render(Receiver receiver, WeakStorage<? extends AbstractBossbar> storage) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        boolean[] blank = blankSlots.computeIfAbsent(receiver.getUniqueId(), _ -> new boolean[MAX_BOSSBARS]);

        for (int i = 0; i < MAX_BOSSBARS; i++) {
            SendableHandle<? extends AbstractBossbar> handle = storage.getFirst(SendableFilter.withData(BOSSBAR_INDEX, i)).orElse(null);

            if (handle == null) {
                if (blank[i]) continue; // Already blank, nothing to re-render
                blank[i] = true;
                sendEventPacket(paperReceiver, i, Component.empty(), BossBarColor.DEFAULT, BossBarOverlay.DEFAULT, Percentage.ZERO);
                continue;
            }

            blank[i] = false;
            SendableState state = handle.state(receiver);
            sendEventPacket(
              paperReceiver,
              i,
              handle.definition().content(state),
              handle.definition().color(state),
              handle.definition().overlay(state),
              handle.definition().progress(state)
            );
        }
    }

    private Optional<BossEvent> getBossEvent(UUID uuid, int index) {
        ConcurrentHashMap<Integer, BossEvent> bossEvents = bossbarIds.get(uuid);
        if (bossEvents == null) return Optional.empty();
        return Optional.ofNullable(bossEvents.get(index));
    }


    private void sendEventPacket(
      PaperReceiver receiver,
      int index,
      Component component,
      BossBarColor color,
      BossBarOverlay overlay,
      Percentage progress
    ) {
        BossEvent bossEvent = getBossEvent(receiver.getUniqueId(), index).orElse(null);
        if (bossEvent == null) return; // Should never happen, we check just in case.

        net.minecraft.network.chat.Component nmsComponent = PaperAdventure.asVanillaNullToEmpty(component);
        BossEvent.BossBarColor nmsColor = switch (color) {
            case PINK -> BossEvent.BossBarColor.PINK;
            case BLUE -> BossEvent.BossBarColor.BLUE;
            case RED -> BossEvent.BossBarColor.RED;
            case GREEN -> BossEvent.BossBarColor.GREEN;
            case YELLOW -> BossEvent.BossBarColor.YELLOW;
            case PURPLE -> BossEvent.BossBarColor.PURPLE;
            case WHITE -> BossEvent.BossBarColor.WHITE;
        };
        BossEvent.BossBarOverlay nmsOverlay = switch (overlay) {
            case PROGRESS -> BossEvent.BossBarOverlay.PROGRESS;
            case NOTCHED_6 -> BossEvent.BossBarOverlay.NOTCHED_6;
            case NOTCHED_10 -> BossEvent.BossBarOverlay.NOTCHED_10;
            case NOTCHED_12 -> BossEvent.BossBarOverlay.NOTCHED_12;
            case NOTCHED_20 -> BossEvent.BossBarOverlay.NOTCHED_20;
        };
        float nmsProgress = progress.get();

        List<Packet<?>> packets = new ArrayList<>();

        boolean hasNameChanged = false;
        boolean hasStyleChanged = false;
        boolean hasProgressChanged = false;


        if (!bossEvent.getName().equals(nmsComponent)) {
            bossEvent.setName(nmsComponent);
            hasNameChanged = true;
        }

        if (bossEvent.getColor() != nmsColor) {
            bossEvent.setColor(nmsColor);
            hasStyleChanged = true;
        }

        if (bossEvent.getOverlay() != nmsOverlay) {
            bossEvent.setOverlay(nmsOverlay);
            hasStyleChanged = true;
        }

        if (bossEvent.getProgress() != nmsProgress) {
            bossEvent.setProgress(nmsProgress);
            hasProgressChanged = true;
        }

        if (hasNameChanged) packets.add(ClientboundBossEventPacket.createUpdateNamePacket(bossEvent));
        if (hasStyleChanged) packets.add(ClientboundBossEventPacket.createUpdateStylePacket(bossEvent));
        if (hasProgressChanged) packets.add(ClientboundBossEventPacket.createUpdateProgressPacket(bossEvent));

        receiver.sendPackets(packets);
    }


    @Override
    protected void startupReceiver(Receiver receiver) {
        super.startupReceiver(receiver);
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        Map<Integer, BossEvent> bossEvents = new ConcurrentHashMap<>();

        for (int i = 0; i < MAX_BOSSBARS; i++) {
            BossEvent bossEvent = new CustomBossEvent(UUID.randomUUID(), Identifier.parse(Bits.key("bossbar_" + i).toString()), CommonComponents.EMPTY, () -> {});
            paperReceiver.sendPackets(ClientboundBossEventPacket.createAddPacket(bossEvent));

            bossEvents.put(i, bossEvent);
        }

        bossbarIds.put(paperReceiver.getUniqueId(), new ConcurrentHashMap<>(bossEvents));
    }

    @Override
    protected void shutdownReceiver(Receiver receiver) {
        super.shutdownReceiver(receiver);
        bossbarIds.remove(receiver.getUniqueId());
        blankSlots.remove(receiver.getUniqueId());
    }

}
