/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable;

import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.exception.BitsException;
import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.lifecycle.manager.ManagerContainer;
import xyz.orbitary.bits.mc.sendable.impl.actionbar.ActionbarManager;
import xyz.orbitary.bits.mc.sendable.impl.bossbar.BossbarManager;
import xyz.orbitary.bits.mc.sendable.impl.sidebar.SidebarManager;
import xyz.orbitary.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.orbitary.bits.mc.sendable.impl.title.TitleManager;
import xyz.orbitary.bits.mc.sendable.impl.waypoint.WaypointManager;

import java.util.Collection;


public abstract class SendableOrchestrator extends ManagerContainer<SendableManager<?>> implements BitsModule {
    private static @Nullable SendableOrchestrator instance;

    private final ActionbarManager actionbarManager = registerManager(createActionbarManager());
    private final BossbarManager bossbarManager = registerManager(createBossbarManager());
    private final SidebarManager sidebarManager = registerManager(createSidebarManager());
    private final TablistManager tablistManager = registerManager(createTablistManager());
    private final TitleManager titleManager = registerManager(createTitleManager());
    private final WaypointManager waypointManager = registerManager(createWaypointManager());


    protected SendableOrchestrator() {
        if (instance != null) throw BitsException.INSTANCE_ALREADY_EXISTS(SendableOrchestrator.class);
        SendableOrchestrator.instance = this;
    }

    public static SendableOrchestrator get() {
        if (instance == null) throw BitsException.INSTANCE_NOT_FOUND(SendableOrchestrator.class);
        return instance;
    }


    public abstract Collection<? extends Receiver> getAllReceivers();


    protected final void tickAll() {
        getAllManagers().forEach(SendableManager::tickAll);
    }


    protected abstract ActionbarManager createActionbarManager();

    public final ActionbarManager actionbar() {
        return actionbarManager;
    }


    protected abstract BossbarManager createBossbarManager();

    public final BossbarManager bossbar() {
        return bossbarManager;
    }


    protected abstract SidebarManager createSidebarManager();

    public final SidebarManager sidebar() {
        return sidebarManager;
    }


    protected abstract TablistManager createTablistManager();

    public final TablistManager tablist() {
        return tablistManager;
    }


    protected abstract TitleManager createTitleManager();

    public final TitleManager title() {
        return titleManager;
    }


    protected abstract WaypointManager createWaypointManager();

    public final WaypointManager waypoint() {
        return waypointManager;
    }

}
