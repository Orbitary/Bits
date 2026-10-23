/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.With;
import net.kyori.adventure.key.Key;

import xyz.orbitary.bits.data.world.Relative;

import java.util.Objects;
import java.util.function.UnaryOperator;


@JsonDeserialize(as = Coord.Record.class)
public interface Coord {

    Key world();

    Pos pos();


    default double x() {
        return pos().x();
    }

    default double y() {
        return pos().y();
    }

    default double z() {
        return pos().z();
    }

    default float yaw() {
        return pos().yaw();
    }

    default float pitch() {
        return pos().pitch();
    }

    default Rotation rotation() {
        return pos().rot();
    }


    //region With
    default Coord withPosition(Pos pos) {
        return new Record(world(), pos);
    }

    default Coord withWorld(Key world) {
        return new Record(world, pos());
    }

    default Coord withVec(Vec3 vec) {
        return mapPosition(p -> p.withVec(vec));
    }

    default Coord withRot(Rotation rot) {
        return mapPosition(p -> p.withRot(rot));
    }

    private Coord mapPosition(UnaryOperator<Pos> mapper) {
        return withPosition(mapper.apply(pos()));
    }
    //endregion


    //region Translation & Rotation
    default Coord translate(Vec3 offset) {
        return mapPosition(p -> p.translate(offset));
    }

    default Coord translate(double dx, double dy, double dz) {
        return mapPosition(p -> p.translate(dx, dy, dz));
    }

    default Coord translate(double amount, Relative relative) {
        return mapPosition(p -> p.translate(amount, relative));
    }

    default Coord lookTowards(Vec3 target) {
        return mapPosition(p -> p.lookTowards(target));
    }
    //endregion


    //region Distance
    default double distanceSquared(Coord other) throws IllegalArgumentException {
        requireSameWorld(other);
        return pos().distanceSquared(other.pos());
    }

    default double distance(Coord other) throws IllegalArgumentException {
        requireSameWorld(other);
        return pos().distance(other.pos());
    }

    /**
     * @return whether {@code other} is within {@code distance}. Coords in different worlds are never within distance.
     */
    default boolean isWithinDistance(Coord other, double distance) {
        return world().equals(other.world()) && pos().vec().isWithinDistance(other.pos().vec(), distance);
    }

    private void requireSameWorld(Coord other) {
        if (!world().equals(other.world())) throw new IllegalArgumentException("Cannot calculate distance between locations in different worlds");
    }
    //endregion


    @With
    record Record(
      @JsonProperty(required = true) Key world,
      @JsonProperty(required = true) Pos pos
    ) implements Coord {
        public Record {
            Objects.requireNonNull(world, "world");
            Objects.requireNonNull(pos, "pos");
        }

    }

}
