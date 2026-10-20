/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import net.kyori.adventure.key.Key;

import xyz.orbitary.bits.data.world.Relative;

import java.util.Objects;
import java.util.function.UnaryOperator;


public record Location(
  Key world,
  Position position
) {
    public Location {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(position, "position");
    }


    //region With
    public Location withPosition(Position position) {
        return new Location(world, position);
    }

    public Location withWorld(Key world) {
        return new Location(world, position);
    }

    private Location mapPosition(UnaryOperator<Position> mapper) {
        return withPosition(mapper.apply(position));
    }
    //endregion


    //region Translation & Rotation
    public Location translate(Vec3 offset) {
        return mapPosition(p -> p.translate(offset));
    }

    public Location translate(double amount, Relative relative) {
        return mapPosition(p -> p.translate(amount, relative));
    }

    public Location lookTowards(Vec3 target) {
        return mapPosition(p -> p.lookTowards(target));
    }
    //endregion


    //region Distance
    public double distanceSquared(Location other) throws IllegalArgumentException {
        requireSameWorld(other);
        return position.distanceSquared(other.position);
    }

    public double distance(Location other) throws IllegalArgumentException {
        requireSameWorld(other);
        return position.distance(other.position);
    }

    private void requireSameWorld(Location other) {
        if (!world.equals(other.world)) throw new IllegalArgumentException("Cannot calculate distance between locations in different worlds");
    }
    //endregion


    //region Conversions
    public BlockPos toBlockPos() {
        return position.toBlockPos();
    }

    public ChunkPos toChunkPos() {
        return toBlockPos().toChunkPos(world);
    }
    //endregion

}
