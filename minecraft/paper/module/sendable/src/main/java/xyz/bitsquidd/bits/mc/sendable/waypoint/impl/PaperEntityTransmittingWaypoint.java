/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.waypoint.impl;

import org.bukkit.entity.LivingEntity;

import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractTransmittingWaypoint;

import java.util.UUID;


public abstract class PaperEntityTransmittingWaypoint extends AbstractTransmittingWaypoint {
    private final LivingEntity livingEntity;

    protected PaperEntityTransmittingWaypoint(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    @Override
    public UUID getTransmitterUUID() {
        return livingEntity.getUniqueId();
    }

}
