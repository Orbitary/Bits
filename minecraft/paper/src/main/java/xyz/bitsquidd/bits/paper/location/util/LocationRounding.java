/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.util;

import org.bukkit.Location;

/**
 * Functional interface for rounding a location to a specific point.
 *
 * @since 0.0.13
 */
@FunctionalInterface
public interface LocationRounding {
    Location apply(Location location);

    LocationRounding NONE = location -> location;

    LocationRounding CENTRE_0Y = location -> {
        location.setX(location.getBlockX() + 0.5f);
        location.setY(location.getBlockY() + 0.0f);
        location.setZ(location.getBlockZ() + 0.5f);
        return location;
    };

    LocationRounding CENTRE = location -> {
        location.setX(location.getBlockX() + 0.5f);
        location.setY(location.getBlockY() + 0.5f);
        location.setZ(location.getBlockZ() + 0.5f);
        return location;
    };

    LocationRounding PIXEL = location -> {
        location.setX(Math.round(location.getX() * 16) / 16.0);
        location.setY(Math.round(location.getY() * 16) / 16.0);
        location.setZ(Math.round(location.getZ() * 16) / 16.0);
        return location;
    };


    //region Combination
    static LocationRounding combined(LocationRounding... roundings) {
        return location -> {
            for (LocationRounding rounding : roundings) {
                location = rounding.apply(location);
            }
            return location;
        };
    }
    //endregion

}
