/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.AbstractActionbar;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.AbstractBossbar;
import xyz.bitsquidd.bits.mc.sendable.impl.sidebar.AbstractSidebar;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.AbstractTablist;
import xyz.bitsquidd.bits.mc.sendable.impl.tablist.data.TablistPosition;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractWaypoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Representation of a group of sendables that can be sent to a {@link Receiver}.
 */
public final class SendableGroup {
    private final List<AbstractActionbar> actionbars = new ArrayList<>();
    private final Map<Integer, AbstractBossbar> bossbars = new HashMap<>();
    private final List<AbstractSidebar> sidebars = new ArrayList<>();
    private final Map<TablistPosition, AbstractTablist> tablists = new HashMap<>();
    private final List<AbstractTitle> titles = new ArrayList<>();
    private final List<AbstractWaypoint> waypoints = new ArrayList<>();


    public static SendableGroup empty() {
        return new SendableGroup();
    }


    public void applyTo(Receiver receiver) {
        actionbars.forEach(receiver::addActionbar);
        bossbars.forEach(receiver::addBossbar);
        sidebars.forEach(receiver::addSidebar);
        tablists.forEach(receiver::addTablist);
        titles.forEach(receiver::addTitle);
        waypoints.forEach(receiver::addWaypoint);
    }

    public void clear() {
        actionbars.clear();
        bossbars.clear();
        sidebars.clear();
        tablists.clear();
        titles.clear();
        waypoints.clear();
    }


    public SendableGroup actionbar(AbstractActionbar actionbar) {
        this.actionbars.add(actionbar);
        return this;
    }

    public SendableGroup bossbar(int index, AbstractBossbar bossbar) {
        this.bossbars.put(index, bossbar);
        return this;
    }

    public SendableGroup sidebar(AbstractSidebar sidebar) {
        this.sidebars.add(sidebar);
        return this;
    }

    public SendableGroup tablist(TablistPosition position, AbstractTablist tablist) {
        this.tablists.put(position, tablist);
        return this;
    }

    public SendableGroup title(AbstractTitle title) {
        this.titles.add(title);
        return this;
    }

    public SendableGroup waypoint(AbstractWaypoint waypoint) {
        this.waypoints.add(waypoint);
        return this;
    }


}
