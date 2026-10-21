/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.key.Key;

import xyz.orbitary.bits.data.world.Relative;

import java.util.Objects;
import java.util.function.UnaryOperator;


public record Location(
  @JsonProperty(required = true) Key world,
  @JsonProperty(required = true) Position pos
) {
    public Location {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(pos, "pos");
    }


    public double x() {
        return pos.x();
    }

    public double y() {
        return pos.y();
    }

    public double z() {
        return pos.z();
    }

    public float yaw() {
        return pos.yaw();
    }

    public float pitch() {
        return pos.pitch();
    }

    public Rotation rotation() {
        return pos.rot();
    }


    //region With
    public Location withPosition(Position position) {
        return new Location(world, position);
    }

    public Location withWorld(Key world) {
        return new Location(world, pos);
    }

    public Location withPos(Vec3 pos) {
        return mapPosition(p -> p.withPos(pos));
    }

    public Location withRot(Rotation rot) {
        return mapPosition(p -> p.withRot(rot));
    }

    private Location mapPosition(UnaryOperator<Position> mapper) {
        return withPosition(mapper.apply(pos));
    }
    //endregion


    //region Translation & Rotation
    public Location translate(Vec3 offset) {
        return mapPosition(p -> p.translate(offset));
    }

    public Location translate(double dx, double dy, double dz) {
        return mapPosition(p -> p.translate(dx, dy, dz));
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
        return pos.distanceSquared(other.pos);
    }

    public double distance(Location other) throws IllegalArgumentException {
        requireSameWorld(other);
        return pos.distance(other.pos);
    }

    /**
     * @return whether {@code other} is within {@code distance}. Locations in different worlds are never within distance.
     */
    public boolean isWithinDistance(Location other, double distance) {
        return world.equals(other.world) && pos.vec().isWithinDistance(other.pos.vec(), distance);
    }

    private void requireSameWorld(Location other) {
        if (!world.equals(other.world)) throw new IllegalArgumentException("Cannot calculate distance between locations in different worlds");
    }
    //endregion


    //region Conversions
    public BlockPos toBlockPos() {
        return pos.toBlockPos();
    }

    public ChunkPos toChunkPos() {
        return toBlockPos().toChunkPos(world);
    }
    //endregion

}
