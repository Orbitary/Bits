/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location.containable.area.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import xyz.orbitary.bits.paper.location.containable.area.TickingArea;

/**
 * Event called when a player enters a {@link TickingArea TickingArea}.
 *
 * @since 0.0.13
 */
public class AreaEnterEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public AreaEnterEvent(Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
