/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.sendable.actionbar.PaperActionbarManager;
import xyz.bitsquidd.bits.mc.sendable.bossbar.PaperBossbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.BossbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.SidebarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.bitsquidd.bits.mc.sendable.impl.title.TitleManager;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.WaypointManager;
import xyz.bitsquidd.bits.mc.sendable.sidebar.PaperSidebarManager;
import xyz.bitsquidd.bits.mc.sendable.tablist.PaperTablistManager;
import xyz.bitsquidd.bits.mc.sendable.title.PaperTitleManager;
import xyz.bitsquidd.bits.mc.sendable.waypoint.PaperWaypointManager;
import xyz.bitsquidd.bits.paper.util.bukkit.listener.PermanentListener;
import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;

import java.util.Collection;


public class PaperSendableOrchestrator extends SendableOrchestrator implements PermanentListener {
    private @Nullable BukkitTask ticker;

    @Override
    public void startup() {
        super.startup();
        ticker = Runnables.buildTimer(this::tickAll, 0, 1)
          .async()
          .forced()
          .run();
    }

    @Override
    public void shutdown() {
        ticker = Runnables.cleanup(ticker);
    }

    @Override
    public Collection<? extends Receiver> getAllReceivers() {
        return Bukkit.getOnlinePlayers().stream().map(PaperReceiver::from).toList();
    }

    @Override
    protected ActionbarManager createActionbarManager() {
        return new PaperActionbarManager();
    }

    @Override
    protected BossbarManager createBossbarManager() {
        return new PaperBossbarManager();
    }

    @Override
    protected SidebarManager createSidebarManager() {
        return new PaperSidebarManager();
    }

    @Override
    protected TablistManager createTablistManager() {
        return new PaperTablistManager();
    }

    @Override
    protected TitleManager createTitleManager() {
        return new PaperTitleManager();
    }

    @Override
    protected WaypointManager createWaypointManager() {
        return new PaperWaypointManager();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getSendableManagers().forEach(manager -> manager.startupReceiver(createReceiver(event.getPlayer())));
    }

    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent event) {
        getSendableManagers().forEach(manager -> manager.shutdownReceiver(createReceiver(event.getPlayer())));
    }


    protected PaperReceiver createReceiver(Player player) {
        return PaperReceiver.from(player);
    }

}
