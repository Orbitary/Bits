/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.lifecycle.manager.ManagerContainer;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.BossbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.SidebarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.bitsquidd.bits.mc.sendable.impl.title.TitleManager;

import java.util.Set;
import java.util.stream.Collectors;


public abstract class SendableOrchestrator extends ManagerContainer {
    private final ActionbarManager actionbarManager = registerManager(createActionbarManager());
    private final BossbarManager bossbarManager = registerManager(createBossbarManager());
    private final SidebarManager sidebarManager = registerManager(createSidebarManager());
    private final TablistManager tablistManager = registerManager(createTablistManager());
    private final TitleManager titleManager = registerManager(createTitleManager());

    private final Set<SendableManager<?, ?>> cachedManagers = getAllManagers().stream().filter(m -> m instanceof SendableManager).map(m -> (SendableManager<?, ?>)m).collect(Collectors.toSet());


    protected final void tickAll() {
        cachedManagers.forEach(SendableManager::tickAll);
    }

    protected abstract ActionbarManager createActionbarManager();

    protected abstract BossbarManager createBossbarManager();

    protected abstract SidebarManager createSidebarManager();

    protected abstract TablistManager createTablistManager();

    protected abstract TitleManager createTitleManager();


}
