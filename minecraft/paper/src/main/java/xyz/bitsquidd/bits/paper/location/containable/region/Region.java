/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3d;

import xyz.bitsquidd.bits.paper.location.containable.Containable;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.RegionVisualiser;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;

import java.util.Set;

/**
 * Represents a region in a world: a defined area that can contain locations and blocks.
 * Implementations are immutable.
 *
 * @since 0.0.13
 */
public abstract class Region implements Containable {
    protected final World world;


    protected Region(World world) {
        this.world = world;
    }


    //region Containment
    public final boolean contains(Location location) {
        if (location == null) return false;
        if (location.getWorld() == null || !location.getWorld().equals(world)) return false;

        return contains(BlockPos.of(location));
    }

    //endregion


    //region Basic getters
    @Override
    public final World world() {
        return world;
    }
    //endregion


    //region Mutators
    public final Region expand(Vector3d amount) {
        return expand(amount.x, amount.y, amount.z);
    }

    public abstract Region expand(double x, double y, double z);


    public final Region shift(Vector3d amount) {
        return shift(amount.x, amount.y, amount.z);
    }

    public abstract Region shift(double x, double y, double z);
    //endregion


    protected abstract Set<RegionVisualiser> createVisualiser();

}
