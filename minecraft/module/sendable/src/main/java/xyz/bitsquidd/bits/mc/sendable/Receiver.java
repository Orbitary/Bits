/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.AbstractActionbar;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.AbstractBossbar;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.AbstractSidebar;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.AbstractTablist;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractWaypoint;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * A receiver of sendables, such as a player.
 * <p>
 * Developer note: Receivers should hashCode() and equals() based on their uniqueId, as this is used for storing sendables in the SendableManager.
 */
public interface Receiver {
    //region General
    UUID getUniqueId();
    //endregion


    //region Generic
    default void removeSendables(SendableFilter<? super Sendable> filter) {
        SendableOrchestrator.get().getSendableManagers().forEach(manager -> manager.getCollection(this).remove(filter));
    }

    default void removeSendables(Class<? extends Sendable> clazz) {
        removeSendables(SendableFilter.ofClass(clazz));
    }

    default List<? extends SendableHandle<? extends Sendable>> getSendables(SendableFilter<? super Sendable> filter) {
        return SendableOrchestrator.get().getSendableManagers().stream()
          .flatMap(manager -> manager.getCollection(this).get(filter).stream())
          .toList();
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractActionbar> List<SendableHandle<S>> getSendables(Class<? extends S> clazz) {
        return getSendables(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Actionbar
    default <S extends AbstractActionbar> Optional<SendableHandle<S>> addActionbar(S actionbar) {
        return SendableOrchestrator.get().actionbar().getCollection(this).add(actionbar, this);
    }

    default void removeActionbar(SendableFilter<? super AbstractActionbar> filter) {
        SendableOrchestrator.get().actionbar().getCollection(this).remove(filter);
    }

    default void removeActionbar(Class<? extends AbstractActionbar> clazz) {
        removeActionbar(SendableFilter.ofClass(clazz));
    }

    default Collection<SendableHandle<AbstractActionbar>> getActionbars(SendableFilter<? super AbstractActionbar> filter) {
        return SendableOrchestrator.get().actionbar().getCollection(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractActionbar> Collection<SendableHandle<S>> getActionbars(Class<? extends S> clazz) {
        return getActionbars(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }

    //endregion


    //region Bossbar
    default <S extends AbstractBossbar> Optional<SendableHandle<S>> addBossbar(Integer index, S bossbar) {
        return SendableOrchestrator.get().bossbar().getCollection(this).add(index, bossbar, this);
    }

    default void removeBossbar(SendableFilter<? super AbstractBossbar> filter) {
        SendableOrchestrator.get().bossbar().getCollection(this).remove(filter);
    }

    default void removeBossbar(Class<? extends AbstractBossbar> clazz) {
        removeBossbar(SendableFilter.ofClass(clazz));
    }

    default Collection<SendableHandle<AbstractBossbar>> getBossbars(SendableFilter<? super AbstractBossbar> filter) {
        return SendableOrchestrator.get().bossbar().getCollection(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractBossbar> Collection<SendableHandle<S>> getBossbars(Class<? extends S> clazz) {
        return getBossbars(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Sidebar
    default <S extends AbstractSidebar> Optional<SendableHandle<S>> addSidebar(S sidebar) {
        return SendableOrchestrator.get().sidebar().getCollection(this).add(sidebar, this);
    }

    default void removeSidebar(SendableFilter<? super AbstractSidebar> filter) {
        SendableOrchestrator.get().sidebar().getCollection(this).remove(filter);
    }

    default void removeSidebar(Class<? extends AbstractSidebar> clazz) {
        removeSidebar(SendableFilter.ofClass(clazz));
    }

    default Collection<SendableHandle<AbstractSidebar>> getSidebars(SendableFilter<? super AbstractSidebar> filter) {
        return SendableOrchestrator.get().sidebar().getCollection(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractSidebar> Collection<SendableHandle<S>> getSidebars(Class<? extends S> clazz) {
        return getSidebars(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Tablist
    default <S extends AbstractTablist> Optional<SendableHandle<S>> addTablist(TablistPosition position, S tablist) {
        return SendableOrchestrator.get().tablist().getCollection(this).add(position, tablist, this);
    }

    default void removeTablist(SendableFilter<? super AbstractTablist> filter) {
        SendableOrchestrator.get().tablist().getCollection(this).remove(filter);
    }

    default void removeTablist(Class<? extends AbstractTablist> clazz) {
        removeTablist(SendableFilter.ofClass(clazz));
    }

    default Collection<SendableHandle<AbstractTablist>> getTablists(SendableFilter<? super AbstractTablist> filter) {
        return SendableOrchestrator.get().tablist().getCollection(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractTablist> Collection<SendableHandle<S>> getTablists(Class<? extends S> clazz) {
        return getTablists(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Title
    default <S extends AbstractTitle> Optional<SendableHandle<S>> addTitle(S title) {
        return SendableOrchestrator.get().title().getCollection(this).add(title, this);
    }

    default void removeTitle(SendableFilter<? super AbstractTitle> filter) {
        SendableOrchestrator.get().title().getCollection(this).remove(filter);
    }

    default void removeTitle(Class<? extends AbstractTitle> clazz) {
        removeTitle(SendableFilter.ofClass(clazz));
    }

    default Collection<SendableHandle<AbstractTitle>> getTitles(SendableFilter<? super AbstractTitle> filter) {
        return SendableOrchestrator.get().title().getCollection(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractTitle> Collection<SendableHandle<S>> getTitles(Class<? extends S> clazz) {
        return getTitles(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Waypoint
    default <S extends AbstractWaypoint> Optional<SendableHandle<S>> addWaypoint(S waypoint) {
        return SendableOrchestrator.get().waypoint().getCollection(this).add(waypoint, this);
    }

    default void removeWaypoint(SendableFilter<? super AbstractWaypoint> filter) {
        SendableOrchestrator.get().waypoint().getCollection(this).remove(filter);
    }

    default void removeWaypoint(Class<? extends AbstractWaypoint> clazz) {
        removeWaypoint(SendableFilter.ofClass(clazz));
    }

    default Collection<SendableHandle<AbstractWaypoint>> getWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        return SendableOrchestrator.get().waypoint().getCollection(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractWaypoint> Collection<SendableHandle<S>> getWaypoints(Class<? extends S> clazz) {
        return getWaypoints(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


}
