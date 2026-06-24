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
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.BossbarManager;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.AbstractSidebar;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.AbstractTablist;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.TablistManager;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractWaypoint;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


/**
 * A receiver of sendables, such as a player.
 * <p>
 * Developer note: Receivers should hashCode() and equals() based on their uniqueId, as this is used for storing sendables in the SendableManager.
 */
@SuppressWarnings("UnusedReturnValue")
public interface Receiver {
    UUID getUniqueId();


    //region Generic

    /**
     * Removes sendables from this receiver based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which sendables to remove.
     *
     * @since 0.0.20
     */
    default void removeSendables(SendableFilter<? super Sendable> filter) {
        SendableOrchestrator.get().getSendableManagers().forEach(manager -> manager.remove(this, filter));
    }

    /**
     * Removes sendables from this receiver based on the provided class type.
     *
     * @param clazz the class type of the sendables to remove.
     *
     * @since 0.0.20
     */
    default void removeSendables(Class<? extends Sendable> clazz) {
        removeSendables(SendableFilter.ofClass(clazz));
    }

    /**
     * Removes the specified sendables from this receiver.
     *
     *
     */
    default void removeSendables(Sendable... sendables) {
        removeSendables(List.of(sendables));
    }

    default void removeSendables(Collection<? extends Sendable> sendables) {
        removeSendables(SendableFilter.ofDefinition(sendables));
    }


    /**
     * Retrieves a list of sendables from this receiver that match the provided {@link SendableFilter filter}.
     * <p>
     * <b>This will not retrieve global sendables currently visible to the player. Use {@link GlobalReceiver#getSendables(SendableFilter)} to retrieve active global sendables.</b>
     *
     * @param filter the filter to determine which sendables to retrieve.
     *
     * @since 0.0.20
     */
    default List<? extends SendableHandle<? extends Sendable>> getSendables(SendableFilter<? super Sendable> filter) {
        return SendableOrchestrator.get().getSendableManagers().stream()
          .flatMap(manager -> manager.getOrCreateCollection(this).get(filter).stream())
          .toList();
    }

    /**
     * Retrieves a list of sendables from this receiver that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the sendables to retrieve.
     *
     * @since 0.0.20
     */
    @SuppressWarnings("unchecked")
    default <S extends Sendable> List<SendableHandle<S>> getSendables(Class<? extends S> clazz) {
        return getSendables(SendableFilter.ofClass(clazz))
          .stream()
          .map(handle -> (SendableHandle<S>)handle)
          .toList();
    }
    //endregion


    //region Actionbar

    /**
     * Adds an actionbar to this receiver and returns a handle for it.
     *
     * @param actionbar the actionbar to add.
     *
     * @since 0.0.20
     */
    default <S extends AbstractActionbar> SendableHandle<S> addActionbar(S actionbar) {
        SendableHandle<S> handle = new SendableHandle<S>(actionbar, this, SendableOrchestrator.get().actionbar());
        SendableOrchestrator.get().actionbar().put(this, handle);
        return handle;
    }

    /**
     * Removes actionbars from this receiver based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which actionbars to remove.
     *
     * @since 0.0.20
     */
    default void removeActionbars(SendableFilter<? super AbstractActionbar> filter) {
        SendableOrchestrator.get().actionbar().remove(this, filter);
    }

    /**
     * Removes actionbars from this receiver based on the provided class type.
     *
     * @param clazz the class type of the actionbars to remove.
     *
     * @since 0.0.20
     */
    default void removeActionbars(Class<? extends AbstractActionbar> clazz) {
        removeActionbars(SendableFilter.ofClass(clazz));
    }

    /**
     * Removes the specified actionbars from this receiver.
     *
     * @param actionbars the actionbars to remove.
     *
     * @since 0.0.20
     */
    default void removeActionbars(AbstractActionbar... actionbars) {
        removeActionbars(List.of(actionbars));
    }

    /**
     * Removes the specified actionbars from this receiver.
     *
     * @param actionbars the actionbars to remove.
     *
     * @since 0.0.20
     */
    default void removeActionbars(Collection<? extends AbstractActionbar> actionbars) {
        removeActionbars(SendableFilter.ofDefinition(actionbars));
    }

    /**
     * Sets the actionbar for this receiver, removing any existing actionbars and adding the provided one.
     * <p>
     * <b>This will not remove global actionbars currently visible to the player. You will have to consider those separately.</b>
     *
     * @param actionbar the actionbar to set for this receiver.
     *
     * @since 0.0.20
     */
    default void setActionbar(AbstractActionbar actionbar) {
        removeActionbars(SendableFilter.alwaysTrue());
        addActionbar(actionbar);
    }

    /**
     * Retrieves a collection of actionbars from this receiver that match the provided {@link SendableFilter filter}.
     * <p>
     * <b>This will not retrieve global actionbars currently visible to the player. Use {@link GlobalReceiver#getActionbars(SendableFilter)} to retrieve active global actionbars.</b>
     *
     * @param filter the filter to determine which actionbars to retrieve.
     *
     * @since 0.0.20
     */
    default Collection<? extends SendableHandle<? extends AbstractActionbar>> getActionbars(SendableFilter<? super AbstractActionbar> filter) {
        return SendableOrchestrator.get().actionbar().get(this, filter);
    }

    /**
     * Retrieves a collection of actionbars from this receiver that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the actionbars to retrieve.
     *
     * @since 0.0.20
     */
    default <S extends AbstractActionbar> Collection<SendableHandle<S>> getActionbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().actionbar().get(this, clazz);
    }

    //endregion


    //region Bossbar

    /**
     * Adds a bossbar to this receiver at the specified index and returns a handle for it.
     *
     * @param index   the index at which to place the bossbar.
     * @param bossbar the bossbar to add.
     *
     * @return a handle for the added bossbar.
     *
     * @since 0.0.20
     */
    default <S extends AbstractBossbar> SendableHandle<S> addBossbar(Integer index, S bossbar) {
        var handle = new SendableHandle<>(bossbar, this, SendableOrchestrator.get().bossbar()).data(BossbarManager.BOSSBAR_INDEX, index);
        SendableOrchestrator.get().bossbar().put(this, handle);
        return handle;
    }

    /**
     * Removes bossbars from this receiver based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which bossbars to remove.
     *
     * @since 0.0.20
     */
    default void removeBossbars(SendableFilter<? super AbstractBossbar> filter) {
        SendableOrchestrator.get().bossbar().remove(this, filter);
    }

    /**
     * Removes bossbars from this receiver based on the provided class type.
     *
     * @param clazz the class type of the bossbars to remove.
     *
     * @since 0.0.20
     */
    default void removeBossbars(Class<? extends AbstractBossbar> clazz) {
        removeBossbars(SendableFilter.ofClass(clazz));
    }

    /**
     * Removes the specified bossbars from this receiver.
     *
     * @param bossbars the bossbars to remove.
     *
     * @since 0.0.20
     */
    default void removeBossbars(AbstractBossbar... bossbars) {
        removeBossbars(List.of(bossbars));
    }

    /**
     * Removes the specified bossbars from this receiver.
     *
     * @param bossbars the bossbars to remove.
     *
     * @since 0.0.20
     */
    default void removeBossbars(Collection<? extends AbstractBossbar> bossbars) {
        removeBossbars(SendableFilter.ofDefinition(bossbars));
    }

    /**
     * Sets the bossbar for this receiver at the specified index, removing any existing bossbars and adding the provided one.
     * <p>
     * <b>This will not remove global bossbars currently visible to the player. You will have to consider those separately.</b>
     *
     * @param index   the index at which to place the bossbar.
     * @param bossbar the bossbar to set for this receiver.
     *
     * @since 0.0.20
     */
    default void setBossbar(Integer index, AbstractBossbar bossbar) {
        removeBossbars(SendableFilter.alwaysTrue());
        addBossbar(index, bossbar);
    }

    /**
     * Retrieves a collection of bossbars from this receiver that match the provided {@link SendableFilter filter}.
     * <p>
     * <b>This will not retrieve global bossbars currently visible to the player. Use {@link GlobalReceiver#getBossbars(SendableFilter)} to retrieve active global bossbars.</b>
     *
     * @param filter the filter to determine which bossbars to retrieve.
     *
     * @return a collection of bossbars matching the filter.
     *
     * @since 0.0.20
     */
    default Collection<? extends SendableHandle<? extends AbstractBossbar>> getBossbars(SendableFilter<? super AbstractBossbar> filter) {
        return SendableOrchestrator.get().bossbar().get(this, filter);
    }

    /**
     * Retrieves a collection of bossbars from this receiver that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the bossbars to retrieve.
     *
     * @return a collection of bossbars matching the class type.
     *
     * @since 0.0.20
     */
    default <S extends AbstractBossbar> Collection<SendableHandle<S>> getBossbars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().bossbar().get(this, clazz);
    }
    //endregion


    //region Sidebar

    /**
     * Adds a sidebar to this receiver and returns a handle for it.
     *
     * @param sidebar the sidebar to add.
     *
     * @return a handle for the added sidebar.
     *
     * @since 0.0.20
     */
    default <S extends AbstractSidebar> SendableHandle<S> addSidebar(S sidebar) {
        var handle = new SendableHandle<>(sidebar, this, SendableOrchestrator.get().sidebar());
        SendableOrchestrator.get().sidebar().put(this, handle);
        return handle;
    }

    /**
     * Removes sidebars from this receiver based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which sidebars to remove.
     *
     * @since 0.0.20
     */
    default void removeSidebars(SendableFilter<? super AbstractSidebar> filter) {
        SendableOrchestrator.get().sidebar().remove(this, filter);
    }

    /**
     * Removes sidebars from this receiver based on the provided class type.
     *
     * @param clazz the class type of the sidebars to remove.
     *
     * @since 0.0.20
     */
    default void removeSidebars(Class<? extends AbstractSidebar> clazz) {
        removeSidebars(SendableFilter.ofClass(clazz));
    }

    /**
     * Removes the specified sidebars from this receiver.
     *
     * @param sidebars the sidebars to remove.
     *
     * @since 0.0.20
     */
    default void removeSidebars(AbstractSidebar... sidebars) {
        removeSidebars(List.of(sidebars));
    }

    /**
     * Removes the specified sidebars from this receiver.
     *
     * @param sidebars the sidebars to remove.
     *
     * @since 0.0.20
     */
    default void removeSidebars(Collection<? extends AbstractSidebar> sidebars) {
        removeSidebars(SendableFilter.ofDefinition(sidebars));
    }

    /**
     * Sets the sidebar for this receiver, removing any existing sidebars and adding the provided one.
     * <p>
     * <b>This will not remove global sidebars currently visible to the player. You will have to consider those separately.</b>
     *
     * @param sidebar the sidebar to set for this receiver.
     *
     * @since 0.0.20
     */
    default void setSidebar(AbstractSidebar sidebar) {
        removeSidebars(SendableFilter.alwaysTrue());
        addSidebar(sidebar);
    }

    /**
     * Retrieves a collection of sidebars from this receiver that match the provided {@link SendableFilter filter}.
     * <p>
     * <b>This will not retrieve global sidebars currently visible to the player. Use {@link GlobalReceiver#getSidebars(SendableFilter)} to retrieve active global sidebars.</b>
     *
     * @param filter the filter to determine which sidebars to retrieve.
     *
     * @return a collection of sidebars matching the filter.
     *
     * @since 0.0.20
     */
    default Collection<? extends SendableHandle<? extends AbstractSidebar>> getSidebars(SendableFilter<? super AbstractSidebar> filter) {
        return SendableOrchestrator.get().sidebar().get(this, filter);
    }

    /**
     * Retrieves a collection of sidebars from this receiver that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the sidebars to retrieve.
     *
     * @return a collection of sidebars matching the class type.
     *
     * @since 0.0.20
     */
    default <S extends AbstractSidebar> Collection<SendableHandle<S>> getSidebars(Class<? extends S> clazz) {
        return SendableOrchestrator.get().sidebar().get(this, clazz);
    }
    //endregion


    //region Tablist

    /**
     * Adds a tablist to this receiver at the specified position and returns a handle for it.
     *
     * @param position the position at which to place the tablist.
     * @param tablist  the tablist to add.
     *
     * @return a handle for the added tablist.
     *
     * @since 0.0.20
     */
    default <S extends AbstractTablist> SendableHandle<S> addTablist(TablistPosition position, S tablist) {
        var handle = new SendableHandle<>(tablist, this, SendableOrchestrator.get().tablist()).data(TablistManager.TABLIST_INDEX, position);
        SendableOrchestrator.get().tablist().put(this, handle);
        return handle;
    }

    /**
     * Removes tablists from this receiver based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which tablists to remove.
     *
     * @since 0.0.20
     */
    default void removeTablists(SendableFilter<? super AbstractTablist> filter) {
        SendableOrchestrator.get().tablist().remove(this, filter);
    }

    /**
     * Removes tablists from this receiver based on the provided class type.
     *
     * @param clazz the class type of the tablists to remove.
     *
     * @since 0.0.20
     */
    default void removeTablists(Class<? extends AbstractTablist> clazz) {
        removeTablists(SendableFilter.ofClass(clazz));
    }

    /**
     * Removes the specified tablists from this receiver.
     *
     * @param tablists the tablists to remove.
     *
     * @since 0.0.20
     */
    default void removeTablists(AbstractTablist... tablists) {
        removeTablists(List.of(tablists));
    }

    /**
     * Removes the specified tablists from this receiver.
     *
     * @param tablists the tablists to remove.
     *
     * @since 0.0.20
     */
    default void removeTablists(Collection<? extends AbstractTablist> tablists) {
        removeTablists(SendableFilter.ofDefinition(tablists));
    }

    /**
     * Sets the tablist for this receiver at the specified position, removing any existing tablists and adding the provided one.
     * <p>
     * <b>This will not remove global tablists currently visible to the player. You will have to consider those separately.</b>
     *
     * @param position the position at which to place the tablist.
     * @param tablist  the tablist to set for this receiver.
     *
     * @since 0.0.20
     */
    default void setTablist(TablistPosition position, AbstractTablist tablist) {
        removeTablists(SendableFilter.alwaysTrue());
        addTablist(position, tablist);
    }

    /**
     * Retrieves a collection of tablists from this receiver that match the provided {@link SendableFilter filter}.
     * <p>
     * <b>This will not retrieve global tablists currently visible to the player. Use {@link GlobalReceiver#getTablists(SendableFilter)} to retrieve active global tablists.</b>
     *
     * @param filter the filter to determine which tablists to retrieve.
     *
     * @return a collection of tablists matching the filter.
     *
     * @since 0.0.20
     */
    default Collection<? extends SendableHandle<? extends AbstractTablist>> getTablists(SendableFilter<? super AbstractTablist> filter) {
        return SendableOrchestrator.get().tablist().get(this, filter);
    }

    /**
     * Retrieves a collection of tablists from this receiver that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the tablists to retrieve.
     *
     * @return a collection of tablists matching the class type.
     *
     * @since 0.0.20
     */
    default <S extends AbstractTablist> Collection<SendableHandle<S>> getTablists(Class<? extends S> clazz) {
        return SendableOrchestrator.get().tablist().get(this, clazz);
    }
    //endregion


    //region Title

    /**
     * Adds a title to this receiver and returns a handle for it.
     *
     * @param title the title to add.
     *
     * @return a handle for the added title.
     *
     * @since 0.0.20
     */
    default <S extends AbstractTitle> SendableHandle<S> addTitle(S title) {
        var handle = new SendableHandle<>(title, this, SendableOrchestrator.get().title());
        SendableOrchestrator.get().title().put(this, handle);
        return handle;
    }

    /**
     * Removes titles from this receiver based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which titles to remove.
     *
     * @since 0.0.20
     */
    default void removeTitles(SendableFilter<? super AbstractTitle> filter) {
        SendableOrchestrator.get().title().remove(this, filter);
    }

    /**
     * Removes titles from this receiver based on the provided class type.
     *
     * @param clazz the class type of the titles to remove.
     *
     * @since 0.0.20
     */
    default void removeTitles(Class<? extends AbstractTitle> clazz) {
        removeTitles(SendableFilter.ofClass(clazz));
    }

    /**
     * Removes the specified titles from this receiver.
     *
     * @param titles the titles to remove.
     *
     * @since 0.0.20
     */
    default void removeTitles(AbstractTitle... titles) {
        removeTitles(List.of(titles));
    }

    /**
     * Removes the specified titles from this receiver.
     *
     * @param titles the titles to remove.
     *
     * @since 0.0.20
     */
    default void removeTitles(Collection<? extends AbstractTitle> titles) {
        removeTitles(SendableFilter.ofDefinition(titles));
    }

    /**
     * Sets the title for this receiver, removing any existing titles and adding the provided one.
     * <p>
     * <b>This will not remove global titles currently visible to the player. You will have to consider those separately.</b>
     *
     * @param title the title to set for this receiver.
     *
     * @since 0.0.20
     */
    default void setTitle(AbstractTitle title) {
        removeTitles(SendableFilter.alwaysTrue());
        addTitle(title);
    }

    /**
     * Retrieves a collection of titles from this receiver that match the provided {@link SendableFilter filter}.
     * <p>
     * <b>This will not retrieve global titles currently visible to the player. Use {@link GlobalReceiver#getTitles(SendableFilter)} to retrieve active global titles.</b>
     *
     * @param filter the filter to determine which titles to retrieve.
     *
     * @return a collection of titles matching the filter.
     *
     * @since 0.0.20
     */
    default Collection<? extends SendableHandle<? extends AbstractTitle>> getTitles(SendableFilter<? super AbstractTitle> filter) {
        return SendableOrchestrator.get().title().get(this, filter);
    }

    /**
     * Retrieves a collection of titles from this receiver that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the titles to retrieve.
     *
     * @return a collection of titles matching the class type.
     *
     * @since 0.0.20
     */
    default <S extends AbstractTitle> Collection<SendableHandle<S>> getTitles(Class<? extends S> clazz) {
        return SendableOrchestrator.get().title().get(this, clazz);
    }
    //endregion


    //region Waypoint

    /**
     * Adds a waypoint to this receiver and returns a handle for it.
     *
     * @param waypoint the waypoint to add.
     *
     * @return a handle for the added waypoint.
     *
     * @since 0.0.20
     */
    default <S extends AbstractWaypoint> SendableHandle<S> addWaypoint(S waypoint) {
        var handle = new SendableHandle<>(waypoint, this, SendableOrchestrator.get().waypoint());
        SendableOrchestrator.get().waypoint().put(this, handle);
        return handle;
    }

    /**
     * Removes waypoints from this receiver based on the provided {@link SendableFilter filter}.
     *
     * @param filter the filter to determine which waypoints to remove.
     *
     * @since 0.0.20
     */
    default void removeWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        SendableOrchestrator.get().waypoint().remove(this, filter);
    }

    /**
     * Removes waypoints from this receiver based on the provided class type.
     *
     * @param clazz the class type of the waypoints to remove.
     *
     * @since 0.0.20
     */
    default void removeWaypoints(Class<? extends AbstractWaypoint> clazz) {
        removeWaypoints(SendableFilter.ofClass(clazz));
    }

    /**
     * Removes the specified waypoints from this receiver.
     *
     * @param waypoints the waypoints to remove.
     *
     * @since 0.0.20
     */
    default void removeWaypoints(AbstractWaypoint... waypoints) {
        removeWaypoints(List.of(waypoints));
    }

    /**
     * Removes the specified waypoints from this receiver.
     *
     * @param waypoints the waypoints to remove.
     *
     * @since 0.0.20
     */
    default void removeWaypoints(Collection<? extends AbstractWaypoint> waypoints) {
        removeWaypoints(SendableFilter.ofDefinition(waypoints));
    }

    /**
     * Sets the waypoint for this receiver, removing any existing waypoints and adding the provided one.
     * <p>
     * <b>This will not remove global waypoints currently visible to the player. You will have to consider those separately.</b>
     *
     * @param waypoint the waypoint to set for this receiver.
     *
     * @since 0.0.20
     */
    default void setWaypoint(AbstractWaypoint waypoint) {
        removeWaypoints(SendableFilter.alwaysTrue());
        addWaypoint(waypoint);
    }

    /**
     * Retrieves a collection of waypoints from this receiver that match the provided {@link SendableFilter filter}.
     * <p>
     * <b>This will not retrieve global waypoints currently visible to the player. Use {@link GlobalReceiver#getWaypoints(SendableFilter)} to retrieve active global waypoints.</b>
     *
     * @param filter the filter to determine which waypoints to retrieve.
     *
     * @return a collection of waypoints matching the filter.
     *
     * @since 0.0.20
     */
    default Collection<? extends SendableHandle<? extends AbstractWaypoint>> getWaypoints(SendableFilter<? super AbstractWaypoint> filter) {
        return SendableOrchestrator.get().waypoint().get(this, filter);
    }

    /**
     * Retrieves a collection of waypoints from this receiver that match the provided class type and casts them to the specified type.
     *
     * @param clazz the class type of the waypoints to retrieve.
     *
     * @return a collection of waypoints matching the class type.
     *
     * @since 0.0.20
     */
    default <S extends AbstractWaypoint> Collection<SendableHandle<S>> getWaypoints(Class<? extends S> clazz) {
        return SendableOrchestrator.get().waypoint().get(this, clazz);
    }
    //endregion


}