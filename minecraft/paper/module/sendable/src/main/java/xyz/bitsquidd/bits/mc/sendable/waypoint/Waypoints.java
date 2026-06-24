/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.waypoint;

import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.joml.Vector3i;

import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.waypoint.AbstractLocationWaypoint;
import xyz.bitsquidd.bits.mc.sendable.waypoint.impl.PaperEntityTransmittingWaypoint;


public final class Waypoints {
    private Waypoints() {}

    public static AbstractLocationWaypoint location(Location location, Key key) {
        return new AbstractLocationWaypoint() {
            @Override
            public Vector3i getPosition(SendableState state) {
                return new Vector3i((int)location.x(), (int)location.y(), (int)location.z());
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
