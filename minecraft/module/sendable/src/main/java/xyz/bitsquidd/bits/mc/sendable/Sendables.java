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

    /**
     * Adds an actionbar to all existing receivers.
     *
     * @param actionbar the actionbar to add to all receivers.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractActionbar> void addActionbar(S actionbar) {
        SendableOrchestrator.get().actionbar().putAll(r -> new SendableHandle<>(actionbar, r, SendableOrchestrator.get().actionbar()));
    }

    /**
     * Retrieves all actionbars across all receivers that match the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which actionbars to retrieve.
     *
     * @return a collection of all actionbars matching the filter.
     *
     * @since 0.0.20
     */
    public static Collection<? extends SendableHandle<? extends AbstractActionbar>> allActionbars(SendableFilter<? super AbstractActionbar> filter) {
        return SendableOrchestrator.get().actionbar().getAll(filter);
    }

    /**
     * Retrieves all actionbars across all receivers that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the actionbars to retrieve.
     *
     * @return a collection of all actionbars matching the class type.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractActionbar> Collection<SendableHandle<S>> allActionbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().actionbar().getAll(clazz);
    }

    /**
     * Removes all actionbars from all receivers based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which actionbars to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllActionbars(SendableFilter<? super AbstractActionbar> filter) {
        SendableOrchestrator.get().actionbar().removeAll(filter);
    }

    /**
     * Removes all actionbars from all receivers based on the provided class type.
     *
     * @param clazz the class type of the actionbars to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllActionbars(Class<? extends AbstractActionbar> clazz) {
        SendableOrchestrator.get().actionbar().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Bossbar

    /**
     * Adds a bossbar to all existing receivers.
     *
     * @param bossbar the bossbar to add to all receivers.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractBossbar> void addBossbar(S bossbar) {
        SendableOrchestrator.get().bossbar().putAll(r -> new SendableHandle<>(bossbar, r, SendableOrchestrator.get().bossbar()));
    }

    /**
     * Retrieves all bossbars across all receivers that match the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which bossbars to retrieve.
     *
     * @return a collection of all bossbars matching the filter.
     *
     * @since 0.0.20
     */
    public static Collection<? extends SendableHandle<? extends AbstractBossbar>> allBossbars(SendableFilter<? super AbstractBossbar> filter) {
        return SendableOrchestrator.get().bossbar().getAll(filter);
    }

    /**
     * Retrieves all bossbars across all receivers that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the bossbars to retrieve.
     *
     * @return a collection of all bossbars matching the class type.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractBossbar> Collection<SendableHandle<S>> allBossbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().bossbar().getAll(clazz);
    }

    /**
     * Removes all bossbars from all receivers based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which bossbars to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllBossbars(SendableFilter<? super AbstractBossbar> filter) {
        SendableOrchestrator.get().bossbar().removeAll(filter);
    }

    /**
     * Removes all bossbars from all receivers based on the provided class type.
     *
     * @param clazz the class type of the bossbars to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllBossbars(Class<? extends AbstractBossbar> clazz) {
        SendableOrchestrator.get().bossbar().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Sidebar

    /**
     * Adds a sidebar to all existing receivers.
     *
     * @param sidebar the sidebar to add to all receivers.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractSidebar> void addSidebar(S sidebar) {
        SendableOrchestrator.get().sidebar().putAll(r -> new SendableHandle<>(sidebar, r, SendableOrchestrator.get().sidebar()));
    }

    /**
     * Retrieves all sidebars across all receivers that match the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which sidebars to retrieve.
     *
     * @return a collection of all sidebars matching the filter.
     *
     * @since 0.0.20
     */
    public static Collection<? extends SendableHandle<? extends AbstractSidebar>> allSidebars(SendableFilter<? super AbstractSidebar> filter) {
        return SendableOrchestrator.get().sidebar().getAll(filter);
    }

    /**
     * Retrieves all sidebars across all receivers that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the sidebars to retrieve.
     *
     * @return a collection of all sidebars matching the class type.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractSidebar> Collection<SendableHandle<S>> allSidebars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().sidebar().getAll(clazz);
    }

    /**
     * Removes all sidebars from all receivers based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which sidebars to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllSidebars(SendableFilter<? super AbstractSidebar> filter) {
        SendableOrchestrator.get().sidebar().removeAll(filter);
    }

    /**
     * Removes all sidebars from all receivers based on the provided class type.
     *
     * @param clazz the class type of the sidebars to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllSidebars(Class<? extends AbstractSidebar> clazz) {
        SendableOrchestrator.get().sidebar().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Tablist

    /**
     * Adds a tablist to all existing receivers.
     *
     * @param tablist the tablist to add to all receivers.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractTablist> void addTablist(S tablist) {
        SendableOrchestrator.get().tablist().putAll(r -> new SendableHandle<>(tablist, r, SendableOrchestrator.get().tablist()));
    }

    /**
     * Retrieves all tablists across all receivers that match the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which tablists to retrieve.
     *
     * @return a collection of all tablists matching the filter.
     *
     * @since 0.0.20
     */
    public static Collection<? extends SendableHandle<? extends AbstractTablist>> allTablists(SendableFilter<? super AbstractTablist> filter) {
        return SendableOrchestrator.get().tablist().getAll(filter);
    }

    /**
     * Retrieves all tablists across all receivers that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the tablists to retrieve.
     *
     * @return a collection of all tablists matching the class type.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractTablist> Collection<SendableHandle<S>> allTablists(Class<? extends S> clazz) {
        return SendableOrchestrator.get().tablist().getAll(clazz);
    }

    /**
     * Removes all tablists from all receivers based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which tablists to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllTablists(SendableFilter<? super AbstractTablist> filter) {
        SendableOrchestrator.get().tablist().removeAll(filter);
    }

    /**
     * Removes all tablists from all receivers based on the provided class type.
     *
     * @param clazz the class type of the tablists to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllTablists(Class<? extends AbstractTablist> clazz) {
        SendableOrchestrator.get().tablist().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Title

    /**
     * Adds a title to all existing receivers.
     *
     * @param title the title to add to all receivers.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractTitle> void addTitle(S title) {
        SendableOrchestrator.get().title().putAll(r -> new SendableHandle<>(title, r, SendableOrchestrator.get().title()));
    }

    /**
     * Retrieves all titles across all receivers that match the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which titles to retrieve.
     *
     * @return a collection of all titles matching the filter.
     *
     * @since 0.0.20
     */
    public static Collection<? extends SendableHandle<? extends AbstractTitle>> allTitles(SendableFilter<? super AbstractTitle> filter) {
        return SendableOrchestrator.get().title().getAll(filter);
    }

    /**
     * Retrieves all titles across all receivers that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the titles to retrieve.
     *
     * @return a collection of all titles matching the class type.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractTitle> Collection<SendableHandle<S>> allTitles(Class<? extends S> clazz) {
        return SendableOrchestrator.get().title().getAll(clazz);
    }

    /**
     * Removes all titles from all receivers based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which titles to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllTitles(SendableFilter<? super AbstractTitle> filter) {
        SendableOrchestrator.get().title().removeAll(filter);
    }

    /**
     * Removes all titles from all receivers based on the provided class type.
     *
     * @param clazz the class type of the titles to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllTitles(Class<? extends AbstractTitle> clazz) {
        SendableOrchestrator.get().title().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion


    //region Waypoint

    /**
     * Adds a waypoint to all existing receivers.
     *
     * @param waypoint the waypoint to add to all receivers.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractWaypoint> void addWaypoint(S waypoint) {
        SendableOrchestrator.get().waypoint().putAll(r -> new SendableHandle<>(waypoint, r, SendableOrchestrator.get().waypoint()));
    }

    /**
     * Retrieves all waypoints across all receivers that match the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which waypoints to retrieve.
     *
     * @return a collection of all waypoints matching the filter.
     *
     * @since 0.0.20
     */
    public static Collection<? extends SendableHandle<? extends AbstractWaypoint>> allWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        return SendableOrchestrator.get().waypoint().getAll(filter);
    }

    /**
     * Retrieves all waypoints across all receivers that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the waypoints to retrieve.
     *
     * @return a collection of all waypoints matching the class type.
     *
     * @since 0.0.20
     */
    public static <S extends AbstractWaypoint> Collection<SendableHandle<S>> allWaypoints(Class<? extends S> clazz) {
        return SendableOrchestrator.get().waypoint().getAll(clazz);
    }

    /**
     * Removes all waypoints from all receivers based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which waypoints to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        SendableOrchestrator.get().waypoint().removeAll(filter);
    }

    /**
     * Removes all waypoints from all receivers based on the provided class type.
     *
     * @param clazz the class type of the waypoints to remove.
     *
     * @since 0.0.20
     */
    public static void removeAllWaypoints(Class<? extends AbstractWaypoint> clazz) {
        SendableOrchestrator.get().waypoint().removeAll(SendableFilter.ofClass(clazz));
    }
    //endregion

}