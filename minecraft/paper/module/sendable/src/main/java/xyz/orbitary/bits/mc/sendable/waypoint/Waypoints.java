/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.sendable.waypoint;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.LivingEntity;

import xyz.orbitary.bits.mc.sendable.impl.SendableState;
import xyz.orbitary.bits.mc.sendable.impl.waypoint.AbstractLocationWaypoint;
import xyz.orbitary.bits.mc.sendable.waypoint.impl.PaperEntityTransmittingWaypoint;
import xyz.orbitary.bits.mc.world.BlockPos;


public final class Waypoints {
    private Waypoints() {}

    public static AbstractLocationWaypoint location(BlockPos pos, Key key) {
        return new AbstractLocationWaypoint() {
            @Override
            public BlockPos getPosition(SendableState state) {
                return pos;
            }

            @Override
            public Key getAssetKey(SendableState state) {
                return key;
            }

        };
    }


    public static PaperEntityTransmittingWaypoint entity(LivingEntity livingEntity, Key key) {
        return new PaperEntityTransmittingWaypoint(livingEntity) {
            @Override
            public Key getAssetKey(SendableState state) {
                return key;
            }
        };
    }


}
