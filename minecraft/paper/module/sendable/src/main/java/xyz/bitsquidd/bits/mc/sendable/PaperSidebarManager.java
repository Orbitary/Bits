/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.SidebarCollection;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.SidebarManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;


public class PaperSidebarManager extends SidebarManager {
    private static final net.minecraft.world.scores.Scoreboard NMS_SCOREBOARD = new net.minecraft.world.scores.Scoreboard();
    private static final String id = UUID.randomUUID().toString().substring(0, 8);
    private static final int MAX_LINES = 15;
    private static final String[] entryNames = IntStream.range(0, MAX_LINES)
      .mapToObj(i -> "line_" + i)
      .toArray(String[]::new);

    private final Map<UUID, Integer> playerLineCount = new ConcurrentHashMap<>();


    @Override
    protected void render(Receiver receiver, SidebarCollection collection) {
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        List<Component> lineComponents = collection.getAll().stream()
          .map(s -> s.definition().content(s.state()))
          .flatMap(Collection::stream)
          .limit(MAX_LINES) // This does mean lower priority sidebars may be cut off
          .toList();

        int newLineCount = lineComponents.size();
        int oldLineCount = playerLineCount.getOrDefault(receiver.getUniqueId(), 0);

        List<Packet<?>> packets = new ArrayList<>();

        // Clear old lines that are no longer needed
        if (oldLineCount > newLineCount) {
            for (int i = newLineCount; i < oldLineCount; i++) {
                int displayPosition = MAX_LINES - 1 - i;
                packets.add(new ClientboundResetScorePacket(
                  entryNames[displayPosition],
                  id
                ));
            }
        }

        // Add/update current lines
        for (int i = 0; i < newLineCount; i++) {
            Component lineComponent = lineComponents.get(i);
            int displayPosition = MAX_LINES - 1 - i;

            packets.add(new ClientboundSetScorePacket(
              entryNames[displayPosition],
              id,
              displayPosition,
              Optional.of(PaperAdventure.asVanillaNullToEmpty(lineComponent)),
              Optional.of(BlankFormat.INSTANCE)
            ));
        }

        playerLineCount.put(receiver.getUniqueId(), newLineCount);

        paperReceiver.sendPackets(packets);
    }


    @Override
    protected void initialiseReceiver(Receiver receiver) {
        super.initialiseReceiver(receiver);
        if (!(receiver instanceof PaperReceiver paperReceiver)) return;

        Objective objective = new Objective(
          NMS_SCOREBOARD,
          id,
          ObjectiveCriteria.DUMMY,
          CommonComponents.EMPTY,
          ObjectiveCriteria.RenderType.INTEGER,
          true,
          null
        );

        List<Packet<?>> packets = List.of(
          new ClientboundSetObjectivePacket(
            objective,
            ClientboundSetObjectivePacket.METHOD_ADD
          ),
          new ClientboundSetDisplayObjectivePacket(
            DisplaySlot.SIDEBAR,
            objective
          )
        );

        paperReceiver.sendPackets(packets);
    }

    @Override
    protected void cleanupReceiver(Receiver receiver) {
        super.cleanupReceiver(receiver);
        playerLineCount.remove(receiver.getUniqueId());
    }


}
