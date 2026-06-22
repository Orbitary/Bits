/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
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
    private final ImmutableList<AbstractActionbar> actionbars;
    private final ImmutableMap<Integer, AbstractBossbar> bossbars;
    private final ImmutableList<AbstractSidebar> sidebars;
    private final ImmutableMap<TablistPosition, AbstractTablist> tablists;
    private final ImmutableList<AbstractTitle> titles;
    private final ImmutableList<AbstractWaypoint> waypoints;

    private SendableGroup(Builder builder) {
        this.actionbars = ImmutableList.copyOf(builder.actionbars);
        this.bossbars = ImmutableMap.copyOf(builder.bossbars);
        this.sidebars = ImmutableList.copyOf(builder.sidebars);
        this.tablists = ImmutableMap.copyOf(builder.tablists);
        this.titles = ImmutableList.copyOf(builder.titles);
        this.waypoints = ImmutableList.copyOf(builder.waypoints);
    }

    public void applyTo(Receiver receiver) {
        actionbars.forEach(receiver::addActionbar);
        bossbars.forEach(receiver::addBossbar);
        sidebars.forEach(receiver::addSidebar);
        tablists.forEach(receiver::addTablist);
        titles.forEach(receiver::addTitle);
        waypoints.forEach(receiver::addWaypoint);
    }

    public void applyGlobal() {
        actionbars.forEach(a -> SendableOrchestrator.get().actionbar().addGlobal(a));
        bossbars.forEach((i, b) -> SendableOrchestrator.get().bossbar().addGlobal(i, b));
        sidebars.forEach(s -> SendableOrchestrator.get().sidebar().addGlobal(s));
        tablists.forEach((p, t) -> SendableOrchestrator.get().tablist().addGlobal(p, t));
        titles.forEach(t -> SendableOrchestrator.get().title().addGlobal(t));
        waypoints.forEach(w -> SendableOrchestrator.get().waypoint().addGlobal(w));
    }

    public void removeGlobal() {
        actionbars.forEach(a -> SendableOrchestrator.get().actionbar().removeAll(h -> h.definition() == a));
        bossbars.forEach((i, b) -> SendableOrchestrator.get().bossbar().removeAll(h -> h.definition() == b));
        sidebars.forEach(s -> SendableOrchestrator.get().sidebar().removeAll(h -> h.definition() == s));
        tablists.forEach((p, t) -> SendableOrchestrator.get().tablist().removeAll(h -> h.definition() == t));
        titles.forEach(t -> SendableOrchestrator.get().title().removeAll(h -> h.definition() == t));
        waypoints.forEach(w -> SendableOrchestrator.get().waypoint().removeAll(h -> h.definition() == w));
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Buildable<SendableGroup> {
        private final List<AbstractActionbar> actionbars = new ArrayList<>();
        private final Map<Integer, AbstractBossbar> bossbars = new HashMap<>();
        private final List<AbstractSidebar> sidebars = new ArrayList<>();
        private final Map<TablistPosition, AbstractTablist> tablists = new HashMap<>();
        private final List<AbstractTitle> titles = new ArrayList<>();
        private final List<AbstractWaypoint> waypoints = new ArrayList<>();

        private Builder() {}


        public Builder actionbar(AbstractActionbar actionbar) {
            this.actionbars.add(actionbar);
            return this;
        }

        public Builder bossbar(int index, AbstractBossbar bossbar) {
            this.bossbars.put(index, bossbar);
            return this;
        }

        public Builder sidebar(AbstractSidebar sidebar) {
            this.sidebars.add(sidebar);
            return this;
        }

        public Builder tablist(TablistPosition position, AbstractTablist tablist) {
            this.tablists.put(position, tablist);
            return this;
        }

        public Builder title(AbstractTitle title) {
            this.titles.add(title);
            return this;
        }

        public Builder waypoint(AbstractWaypoint waypoint) {
            this.waypoints.add(waypoint);
            return this;
        }


        @Override
        public SendableGroup build() {
            return new SendableGroup(this);
        }

    }


}
