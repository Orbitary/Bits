/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.region;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bukkit.World;
import org.joml.Vector3d;

import xyz.bitsquidd.bits.paper.location.containable.Containable;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.RegionVisualiser;

import java.util.Set;

/**
 * Represents a region in a world: a defined area that can contain locations and blocks.
 * Implementations are immutable.
 *
 * @since 0.0.13
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CylinderRegion.class, name = "cylinder"),
  @JsonSubTypes.Type(value = CuboidRegion.class, name = "cuboid"),
  @JsonSubTypes.Type(value = EllipsoidRegion.class, name = "ellipsoid")
})
public abstract class Region implements Containable {
    protected final World world;


    protected Region(World world) {
        this.world = world;
    }


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
