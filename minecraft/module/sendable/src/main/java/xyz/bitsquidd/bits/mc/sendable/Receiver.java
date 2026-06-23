/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.mc.sendable.collection.OperationSuite;
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
@SuppressWarnings("UnusedReturnValue")
public interface Receiver {
    //region General
    UUID getUniqueId();
    //endregion


    //region Generic
    default void removeSendables(SendableFilter<? super Sendable> filter) {
        SendableOrchestrator.get().getSendableManagers().forEach(manager -> manager.remove(this, filter));
    }

    default void removeSendables(Class<? extends Sendable> clazz) {
        removeSendables(SendableFilter.ofClass(clazz));
    }

    default List<? extends SendableHandle<? extends Sendable>> getSendables(SendableFilter<? super Sendable> filter) {
        return SendableOrchestrator.get().getSendableManagers().stream()
          .flatMap(manager -> manager.getOrCreateCollection(this).get(filter).stream())
          .toList();
    }

    @SuppressWarnings("unchecked")
    default <S extends Sendable> List<SendableHandle<S>> getSendables(Class<? extends S> clazz) {
        return getSendables(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Actionbar
    @SuppressWarnings("unchecked")
    default <S extends AbstractActionbar> Optional<SendableHandle<S>> addActionbar(S actionbar) {
        return (Optional<SendableHandle<S>>)(Optional<?>)SendableOrchestrator.get().actionbar().put(this, new OperationSuite.Multiple<>(new SendableHandle<>(actionbar, this, SendableOrchestrator.get().actionbar())));
    }

    default void removeActionbar(SendableFilter<? super AbstractActionbar> filter) {
        SendableOrchestrator.get().actionbar().remove(this, filter);
    }

    default void setActionbar(AbstractActionbar actionbar) {
        removeActionbar(SendableFilter.alwaysTrue());
        addActionbar(actionbar);
    }

    default Collection<SendableHandle<AbstractActionbar>> getActionbars(SendableFilter<? super AbstractActionbar> filter) {
        return SendableOrchestrator.get().actionbar().get(this, filter);
    }

    default <S extends AbstractActionbar> Collection<SendableHandle<S>> getActionbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().actionbar().get(this, clazz);
    }

    //endregion


    //region Bossbar
    @SuppressWarnings("unchecked")
    default <S extends AbstractBossbar> Optional<SendableHandle<S>> addBossbar(Integer index, S bossbar) {
        return (Optional<SendableHandle<S>>)(Optional<?>)SendableOrchestrator.get().bossbar().put(this, new OperationSuite.Keyed<>(index, new SendableHandle<>(bossbar, this, SendableOrchestrator.get().bossbar())));
    }

    default void removeBossbar(SendableFilter<? super AbstractBossbar> filter) {
        SendableOrchestrator.get().bossbar().remove(this, filter);
    }

    default void setBossbar(Integer index, AbstractBossbar bossbar) {
        removeBossbar(SendableFilter.alwaysTrue());
        addBossbar(index, bossbar);
    }


    default Collection<SendableHandle<AbstractBossbar>> getBossbars(SendableFilter<? super AbstractBossbar> filter) {
        return SendableOrchestrator.get().bossbar().get(this, filter);
    }

    default <S extends AbstractBossbar> Collection<SendableHandle<S>> getBossbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().bossbar().get(this, clazz);
    }
    //endregion


    //region Sidebar
    @SuppressWarnings("unchecked")
    default <S extends AbstractSidebar> Optional<SendableHandle<S>> addSidebar(S sidebar) {
        return (Optional<SendableHandle<S>>)(Optional<?>)SendableOrchestrator.get().sidebar().put(this, new OperationSuite.Multiple<>(new SendableHandle<>(sidebar, this, SendableOrchestrator.get().sidebar())));
    }

    default void removeSidebar(SendableFilter<? super AbstractSidebar> filter) {
        SendableOrchestrator.get().sidebar().remove(this, filter);
    }

    default void setSidebar(AbstractSidebar sidebar) {
        removeSidebar(SendableFilter.alwaysTrue());
        addSidebar(sidebar);
    }

    default Collection<SendableHandle<AbstractSidebar>> getSidebars(SendableFilter<? super AbstractSidebar> filter) {
        return SendableOrchestrator.get().sidebar().get(this, filter);
    }

    default <S extends AbstractSidebar> Collection<SendableHandle<S>> getSidebars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().sidebar().get(this, clazz);
    }
    //endregion


    //region Tablist
    @SuppressWarnings("unchecked")
    default <S extends AbstractTablist> Optional<SendableHandle<S>> addTablist(TablistPosition position, S tablist) {
        return (Optional<SendableHandle<S>>)(Optional<?>)SendableOrchestrator.get().tablist().put(this, new OperationSuite.Keyed<>(position, new SendableHandle<>(tablist, this, SendableOrchestrator.get().tablist())));
    }

    default void removeTablist(SendableFilter<? super AbstractTablist> filter) {
        SendableOrchestrator.get().tablist().remove(this, filter);
    }

    default void setTablist(TablistPosition position, AbstractTablist tablist) {
        removeTablist(SendableFilter.alwaysTrue());
        addTablist(position, tablist);
    }

    default Collection<SendableHandle<AbstractTablist>> getTablists(SendableFilter<? super AbstractTablist> filter) {
        return SendableOrchestrator.get().tablist().get(this, filter);
    }

    default <S extends AbstractTablist> Collection<SendableHandle<S>> getTablists(Class<? extends S> clazz) {
        return SendableOrchestrator.get().tablist().get(this, clazz);
    }
    //endregion


    //region Title
    @SuppressWarnings("unchecked")
    default <S extends AbstractTitle> Optional<SendableHandle<S>> addTitle(S title) {
        return (Optional<SendableHandle<S>>)(Optional<?>)SendableOrchestrator.get().title().put(this, new OperationSuite.Multiple<>(new SendableHandle<>(title, this, SendableOrchestrator.get().title())));
    }

    default void removeTitle(SendableFilter<? super AbstractTitle> filter) {
        SendableOrchestrator.get().title().remove(this, filter);
    }

    default void setTitle(AbstractTitle title) {
        removeTitle(SendableFilter.alwaysTrue());
        addTitle(title);
    }

    default Collection<SendableHandle<AbstractTitle>> getTitles(SendableFilter<? super AbstractTitle> filter) {
        return SendableOrchestrator.get().title().get(this, filter);
    }

    default <S extends AbstractTitle> Collection<SendableHandle<S>> getTitles(Class<? extends S> clazz) {
        return SendableOrchestrator.get().title().get(this, clazz);
    }
    //endregion


    //region Waypoint
    @SuppressWarnings("unchecked")
    default <S extends AbstractWaypoint> Optional<SendableHandle<S>> addWaypoint(S waypoint) {
        return (Optional<SendableHandle<S>>)(Optional<?>)SendableOrchestrator.get().waypoint().put(this, new OperationSuite.Multiple<>(new SendableHandle<>(waypoint, this, SendableOrchestrator.get().waypoint())));
    }

    default void removeWaypoint(SendableFilter<? super AbstractWaypoint> filter) {
        SendableOrchestrator.get().waypoint().remove(this, filter);
    }

    default void setWaypoint(AbstractWaypoint waypoint) {
        removeWaypoint(SendableFilter.alwaysTrue());
        addWaypoint(waypoint);
    }

    default Collection<SendableHandle<AbstractWaypoint>> getWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        return SendableOrchestrator.get().waypoint().get(this, filter);
    }

    default <S extends AbstractWaypoint> Collection<SendableHandle<S>> getWaypoints(Class<? extends S> clazz) {
        return SendableOrchestrator.get().waypoint().get(this, clazz);
    }
    //endregion


}
