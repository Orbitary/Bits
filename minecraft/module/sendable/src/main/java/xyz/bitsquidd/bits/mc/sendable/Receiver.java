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

import java.util.Collection;
import java.util.List;
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
        SendableOrchestrator.get().getSendableManagers().forEach(manager -> manager.getSendables(this).remove(filter));
    }

    default List<? extends SendableHandle<? extends Sendable>> getSendables(SendableFilter<? super Sendable> filter) {
        return SendableOrchestrator.get().getSendableManagers().stream()
          .flatMap(manager -> manager.getSendables(this).get(filter).stream())
          .toList();
    }
    //endregion


    //region Actionbar
    default void addActionbar(AbstractActionbar actionbar) {
        SendableOrchestrator.get().actionbar().getSendables(this).add(actionbar);
    }

    default void removeActionbar(SendableFilter<? super AbstractActionbar> filter) {
        SendableOrchestrator.get().actionbar().getSendables(this).remove(filter);
    }

    default Collection<? extends SendableHandle<AbstractActionbar>> getActionbars(SendableFilter<? super AbstractActionbar> filter) {
        return SendableOrchestrator.get().actionbar().getSendables(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractActionbar> Collection<? extends SendableHandle<S>> getActionbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().actionbar().getSendables(this)
          .get(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }

    //endregion


    //region Bossbar
    default void addBossbar(Integer index, AbstractBossbar bossbar) {
        SendableOrchestrator.get().bossbar().getSendables(this).add(index, bossbar);
    }

    default void removeBossbar(SendableFilter<? super AbstractBossbar> filter) {
        SendableOrchestrator.get().bossbar().getSendables(this).remove(filter);
    }

    default Collection<? extends SendableHandle<AbstractBossbar>> getBossbars(SendableFilter<? super AbstractBossbar> filter) {
        return SendableOrchestrator.get().bossbar().getSendables(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractBossbar> Collection<? extends SendableHandle<S>> getBossbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().bossbar().getSendables(this)
          .get(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Sidebar
    default void addSidebar(AbstractSidebar sidebar) {
        SendableOrchestrator.get().sidebar().getSendables(this).add(sidebar);
    }

    default void removeSidebar(SendableFilter<? super AbstractSidebar> filter) {
        SendableOrchestrator.get().sidebar().getSendables(this).remove(filter);
    }

    default Collection<? extends SendableHandle<AbstractSidebar>> getSidebars(SendableFilter<? super AbstractSidebar> filter) {
        return SendableOrchestrator.get().sidebar().getSendables(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractSidebar> Collection<? extends SendableHandle<S>> getSidebars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().sidebar().getSendables(this)
          .get(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Tablist
    default void addTablist(TablistPosition position, AbstractTablist tablist) {
        SendableOrchestrator.get().tablist().getSendables(this).add(position, tablist);
    }

    default void removeTablist(SendableFilter<? super AbstractTablist> filter) {
        SendableOrchestrator.get().tablist().getSendables(this).remove(filter);
    }

    default Collection<? extends SendableHandle<AbstractTablist>> getTablists(SendableFilter<? super AbstractTablist> filter) {
        return SendableOrchestrator.get().tablist().getSendables(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractTablist> Collection<? extends SendableHandle<S>> getTablists(Class<? extends S> clazz) {
        return SendableOrchestrator.get().tablist().getSendables(this)
          .get(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Title
    default void addTitle(AbstractTitle title) {
        SendableOrchestrator.get().title().getSendables(this).add(title);
    }

    default void removeTitle(SendableFilter<? super AbstractTitle> filter) {
        SendableOrchestrator.get().title().getSendables(this).remove(filter);
    }

    default Collection<? extends SendableHandle<AbstractTitle>> getTitles(SendableFilter<? super AbstractTitle> filter) {
        return SendableOrchestrator.get().title().getSendables(this).get(filter);
    }

    @SuppressWarnings("unchecked")
    default <S extends AbstractTitle> Collection<? extends SendableHandle<S>> getTitles(Class<? extends S> clazz) {
        return SendableOrchestrator.get().title().getSendables(this)
          .get(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


}
