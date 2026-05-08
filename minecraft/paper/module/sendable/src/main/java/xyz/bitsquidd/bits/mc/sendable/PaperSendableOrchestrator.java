/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.sendable.actionbar.PaperActionbarManager;
import xyz.bitsquidd.bits.mc.sendable.bossbar.PaperBossbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.BossbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.SidebarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.bitsquidd.bits.mc.sendable.impl.title.TitleManager;
import xyz.bitsquidd.bits.mc.sendable.sidebar.PaperSidebarManager;
import xyz.bitsquidd.bits.mc.sendable.tablist.PaperTablistManager;
import xyz.bitsquidd.bits.mc.sendable.title.PaperTitleManager;
import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;


public class PaperSendableOrchestrator extends SendableOrchestrator {
    private @Nullable BukkitTask ticker;
    private @Nullable BukkitTask renderer;

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
        renderer = Runnables.cleanup(renderer);
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


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getSendableManagers().forEach(manager -> manager.initialiseReceiver(PaperReceiver.from(event.getPlayer())));
    }

    @EventHandler
    public final void onPlayerQuit(PlayerJoinEvent event) {
        getSendableManagers().forEach(manager -> manager.cleanupReceiver(PaperReceiver.from(event.getPlayer())));
    }

}
