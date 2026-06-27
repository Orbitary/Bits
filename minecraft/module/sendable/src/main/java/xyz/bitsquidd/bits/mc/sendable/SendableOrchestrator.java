/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.exception.BitsException;
import xyz.bitsquidd.bits.lifecycle.manager.BitsModule;
import xyz.bitsquidd.bits.lifecycle.manager.ManagerContainer;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.ActionbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.BossbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.SidebarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.bitsquidd.bits.mc.sendable.impl.title.TitleManager;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.WaypointManager;

import java.util.Collection;
import java.util.Set;


public abstract class SendableOrchestrator extends ManagerContainer implements BitsModule {
    private static @Nullable SendableOrchestrator instance;

    private final ActionbarManager actionbarManager = registerManager(createActionbarManager());
    private final BossbarManager bossbarManager = registerManager(createBossbarManager());
    private final SidebarManager sidebarManager = registerManager(createSidebarManager());
    private final TablistManager tablistManager = registerManager(createTablistManager());
    private final TitleManager titleManager = registerManager(createTitleManager());
    private final WaypointManager waypointManager = registerManager(createWaypointManager());

    private final ImmutableSet<SendableManager<?>> cachedManagers = ImmutableSet.copyOf(getAllManagers().stream().filter(m -> m instanceof SendableManager).map(m -> (SendableManager<?>)m).toList());

    protected SendableOrchestrator() {
        if (instance != null) throw BitsException.INSTANCE_ALREADY_EXISTS(SendableOrchestrator.class);
        SendableOrchestrator.instance = this;
    }

    public static SendableOrchestrator get() {
        if (instance == null) throw BitsException.INSTANCE_NOT_FOUND(SendableOrchestrator.class);
        return instance;
    }


    @Unmodifiable
    public final Set<SendableManager<?>> getSendableManagers() {
        return cachedManagers;
    }


    public abstract Collection<? extends Receiver> getAllReceivers();


    protected final void tickAll() {
        getSendableManagers().forEach(SendableManager::tickAll);
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
