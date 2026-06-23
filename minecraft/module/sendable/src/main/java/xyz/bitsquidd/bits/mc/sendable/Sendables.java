/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.AbstractActionbar;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.AbstractBossbar;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.AbstractSidebar;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.AbstractTablist;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractWaypoint;

import java.util.Collection;


/**
 * Mass operation sendable utilities.
 * <p>
 * Developer Note: Prefer {@link GlobalReceiver} for global sendables (that persist for new receivers), rather than add methods here - which will only add to existing receivers.
 */
public final class Sendables {
    private Sendables() {}


    //region Actionbar
    public static <S extends AbstractActionbar> void addActionbar(S actionbar) {
        SendableOrchestrator.get().actionbar()
          .putAll(r -> new SendableHandle<>(actionbar, r, SendableOrchestrator.get().actionbar()));
    }


    public static Collection<? extends SendableHandle<? extends AbstractActionbar>> allActionbars(SendableFilter<? super AbstractActionbar> filter) {
        return SendableOrchestrator.get().actionbar().getAll(filter);
    }

    public static <S extends AbstractActionbar> Collection<SendableHandle<S>> allActionbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().actionbar().getAll(clazz);
    }


    public static void removeAllActionbars(SendableFilter<? super AbstractActionbar> filter) {
        SendableOrchestrator.get().actionbar().removeAll(filter);
    }

    public static void removeAllActionbars(Class<? extends AbstractActionbar> clazz) {
        SendableOrchestrator.get().actionbar().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Bossbar
    public static <S extends AbstractBossbar> void addBossbar(S bossbar) {
        SendableOrchestrator.get().bossbar()
          .putAll(r -> new SendableHandle<>(bossbar, r, SendableOrchestrator.get().bossbar()));
    }


    public static Collection<? extends SendableHandle<? extends AbstractBossbar>> allBossbars(SendableFilter<? super AbstractBossbar> filter) {
        return SendableOrchestrator.get().bossbar().getAll(filter);
    }

    public static <S extends AbstractBossbar> Collection<SendableHandle<S>> allBossbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().bossbar().getAll(clazz);
    }


    public static void removeAllBossbars(SendableFilter<? super AbstractBossbar> filter) {
        SendableOrchestrator.get().bossbar().removeAll(filter);
    }

    public static void removeAllBossbars(Class<? extends AbstractBossbar> clazz) {
        SendableOrchestrator.get().bossbar().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Sidebar
    public static <S extends AbstractSidebar> void addSidebar(S sidebar) {
        SendableOrchestrator.get().sidebar()
          .putAll(r -> new SendableHandle<>(sidebar, r, SendableOrchestrator.get().sidebar()));
    }


    public static Collection<? extends SendableHandle<? extends AbstractSidebar>> allSidebars(SendableFilter<? super AbstractSidebar> filter) {
        return SendableOrchestrator.get().sidebar().getAll(filter);
    }

    public static <S extends AbstractSidebar> Collection<SendableHandle<S>> allSidebars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().sidebar().getAll(clazz);
    }


    public static void removeAllSidebars(SendableFilter<? super AbstractSidebar> filter) {
        SendableOrchestrator.get().sidebar().removeAll(filter);
    }

    public static void removeAllSidebars(Class<? extends AbstractSidebar> clazz) {
        SendableOrchestrator.get().sidebar().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Tablist
    public static <S extends AbstractTablist> void addTablist(S tablist) {
        SendableOrchestrator.get().tablist()
          .putAll(r -> new SendableHandle<>(tablist, r, SendableOrchestrator.get().tablist()));
    }


    public static Collection<? extends SendableHandle<? extends AbstractTablist>> allTablists(SendableFilter<? super AbstractTablist> filter) {
        return SendableOrchestrator.get().tablist().getAll(filter);
    }

    public static <S extends AbstractTablist> Collection<SendableHandle<S>> allTablists(Class<? extends S> clazz) {
        return SendableOrchestrator.get().tablist().getAll(clazz);
    }


    public static void removeAllTablists(SendableFilter<? super AbstractTablist> filter) {
        SendableOrchestrator.get().tablist().removeAll(filter);
    }

    public static void removeAllTablists(Class<? extends AbstractTablist> clazz) {
        SendableOrchestrator.get().tablist().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Title
    public static <S extends AbstractTitle> void addTitle(S title) {
        SendableOrchestrator.get().title()
          .putAll(r -> new SendableHandle<>(title, r, SendableOrchestrator.get().title()));
    }


    public static Collection<? extends SendableHandle<? extends AbstractTitle>> allTitles(SendableFilter<? super AbstractTitle> filter) {
        return SendableOrchestrator.get().title().getAll(filter);
    }

    public static <S extends AbstractTitle> Collection<SendableHandle<S>> allTitles(Class<? extends S> clazz) {
        return SendableOrchestrator.get().title().getAll(clazz);
    }


    public static void removeAllTitles(SendableFilter<? super AbstractTitle> filter) {
        SendableOrchestrator.get().title().removeAll(filter);
    }

    public static void removeAllTitles(Class<? extends AbstractTitle> clazz) {
        SendableOrchestrator.get().title().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Waypoint
    public static <S extends AbstractWaypoint> void addWaypoint(S waypoint) {
        SendableOrchestrator.get().waypoint()
          .putAll(r -> new SendableHandle<>(waypoint, r, SendableOrchestrator.get().waypoint()));
    }


    public static Collection<? extends SendableHandle<? extends AbstractWaypoint>> allWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        return SendableOrchestrator.get().waypoint().getAll(filter);
    }

    public static <S extends AbstractWaypoint> Collection<SendableHandle<S>> allWaypoints(Class<? extends S> clazz) {
        return SendableOrchestrator.get().waypoint().getAll(clazz);
    }


    public static void removeAllWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        SendableOrchestrator.get().waypoint().removeAll(filter);
    }

    public static void removeAllWaypoints(Class<? extends AbstractWaypoint> clazz) {
        SendableOrchestrator.get().waypoint().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion

}