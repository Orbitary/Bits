/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.region;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import xyz.bitsquidd.bits.paper.location.containable.Containable;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.RegionVisualiser;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;
import xyz.bitsquidd.bits.paper.location.wrapper.ChunkCoordinate;

import java.util.HashSet;
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

    protected final Quaternionf rotation;

    protected Region(World world) {
        this(world, new Quaternionf());
    }

    protected Region(World world, Quaternionf rotation) {
        this.world = world;
        this.rotation = new Quaternionf(rotation);
    }


    //region Basic getters
    @Override
    public final World world() {
        return world;
    }

    public final Quaternionf rotation() {
        return new Quaternionf(rotation);
    }
    //endregion


    protected final Vector3d toLocal(double x, double y, double z, double centerX, double centerY, double centerZ) {
        Vector3d local = new Vector3d(x - centerX, y - centerY, z - centerZ);
        if (!rotation.equals(new Quaternionf(), 1.0e-6f)) new Quaternionf(rotation).conjugate().transform(local);
        return local;
    }


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


    //region Chunks
    @Override
    public final Set<ChunkCoordinate> getChunks() {
        BlockPos c = center();
        BlockPos mn = min();
        BlockPos mx = max();

        double halfX = (mx.x - mn.x) / 2.0;
        double halfY = (mx.y - mn.y) / 2.0;
        double halfZ = (mx.z - mn.z) / 2.0;

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sy = -1; sy <= 1; sy += 2) {
                for (int sz = -1; sz <= 1; sz += 2) {
                    Vector3d corner = new Vector3d(sx * halfX, sy * halfY, sz * halfZ);
                    rotation.transform(corner);
                    minX = Math.min(minX, c.x + corner.x);
                    maxX = Math.max(maxX, c.x + corner.x);
                    minZ = Math.min(minZ, c.z + corner.z);
                    maxZ = Math.max(maxZ, c.z + corner.z);
                }
            }
        }

        int minChunkX = (int)Math.floor(minX) >> 4, maxChunkX = (int)Math.floor(maxX) >> 4;
        int minChunkZ = (int)Math.floor(minZ) >> 4, maxChunkZ = (int)Math.floor(maxZ) >> 4;

        Key worldKey = world.key();
        Set<ChunkCoordinate> chunks = new HashSet<>();
        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                chunks.add(new ChunkCoordinate(x, z, worldKey));
            }
        }
        return chunks;
    }
    //endregion


    // Implementations should be done yourself, not a good fit for raw regions.
    @Deprecated(forRemoval = true)
    protected abstract Set<RegionVisualiser> createVisualiser();

}
