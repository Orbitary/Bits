/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.paper.location.wrapper.BlockPos;
import xyz.orbitary.bits.paper.location.wrapper.Locatable;

import java.util.Collection;


/**
 * Suite of location-related utility methods.
 *
 * @since 0.0.11
 */
public final class Locations {
    private Locations() {}


    //region Utils
    public static boolean isSameWorld(Location... locations) {
        if (locations.length == 0) return false;
        World world = locations[0].getWorld();

        for (Location location : locations) {
            if (!location.getWorld().equals(world)) return false;
        }
        return true;
    }

    //endregion


    //region Distance

    /**
     * Safe distance check between two locations.
     * Returns false if either location is null or they are in different worlds.
     *
     * @since 0.0.11
     */
    public static boolean isWithinDistance(@Nullable Location loc1, @Nullable Location loc2, double distance) {
        return isWithinDistanceSq(loc1, loc2, distance * distance);
    }

    public static boolean isWithinDistanceSq(@Nullable Location loc1, @Nullable Location loc2, double distanceSq) {
        if (loc1 == null || loc2 == null) return false;
        return getDistanceSq(loc1, loc2) <= distanceSq;
    }

    public static double getDistance(Location location1, Location location2) {
        if (!isSameWorld(location1, location2)) return Double.MAX_VALUE;
        return Math.sqrt(getDistanceSq(location1, location2));
    }

    public static double getDistanceSq(Location location1, Location location2) {
        if (!isSameWorld(location1, location2)) return Double.MAX_VALUE;

        double dx = location1.getX() - location2.getX();
        double dy = location1.getY() - location2.getY();
        double dz = location1.getZ() - location2.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    public static double getHorizontalDistance(Location location1, Location location2) {
        return Math.sqrt(getHorizontalDistanceSq(location1, location2));
    }

    public static double getHorizontalDistanceSq(Location location1, Location location2) {
        if (!isSameWorld(location1, location2)) return Double.MAX_VALUE;

        double dx = location1.getX() - location2.getX();
        double dz = location1.getZ() - location2.getZ();
        return dx * dx + dz * dz;
    }
    //endregion


    //region Collection Operations

    /**
     * Calculates the midpoint BlockPos coordinates from a collection of {@link Locatable}s.
     *
     * @since 0.0.13
     */
    public static BlockPos getMidpoint(Collection<? extends Locatable> locatables) {
        if (locatables.isEmpty()) throw new IllegalArgumentException("Locatables cannot be empty");

        int amount = locatables.size();
        double sumX = locatables.stream().mapToDouble(l -> l.asVector().getX()).sum();
        double sumY = locatables.stream().mapToDouble(l -> l.asVector().getY()).sum();
        double sumZ = locatables.stream().mapToDouble(l -> l.asVector().getZ()).sum();

        return BlockPos.of(sumX / amount, sumY / amount, sumZ / amount);
    }

    /**
     * Calculates the minimum BlockPos coordinates from a collection of {@link Locatable}s.
     *
     * @since 0.0.13
     */
    public static BlockPos getMinLocation(Collection<? extends Locatable> locatables) {
        if (locatables.isEmpty()) throw new IllegalArgumentException("Locatables cannot be empty");

        return BlockPos.of(
          locatables.stream().mapToDouble(l -> l.asVector().getX()).min().orElseThrow(() -> new IllegalArgumentException("Error computing min x")),
          locatables.stream().mapToDouble(l -> l.asVector().getY()).min().orElseThrow(() -> new IllegalArgumentException("Error computing min y")),
          locatables.stream().mapToDouble(l -> l.asVector().getZ()).min().orElseThrow(() -> new IllegalArgumentException("Error computing min z"))
        );
    }

    /**
     * Calculates the maximum BlockPos coordinates from a collection of {@link Locatable}s.
     *
     * @since 0.0.13
     */
    public static BlockPos getMaxLocation(Collection<? extends Locatable> locatables) {
        if (locatables.isEmpty()) throw new IllegalArgumentException("Locatables cannot be empty");

        return BlockPos.of(
          locatables.stream().mapToDouble(l -> l.asVector().getX()).max().orElseThrow(() -> new IllegalArgumentException("Error computing max x")),
          locatables.stream().mapToDouble(l -> l.asVector().getY()).max().orElseThrow(() -> new IllegalArgumentException("Error computing max y")),
          locatables.stream().mapToDouble(l -> l.asVector().getZ()).max().orElseThrow(() -> new IllegalArgumentException("Error computing max z"))
        );
    }
    //endregion

}