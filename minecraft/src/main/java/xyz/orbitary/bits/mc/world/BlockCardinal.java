/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world;

import com.fasterxml.jackson.annotation.JsonProperty;

import xyz.orbitary.bits.data.world.Cardinal;

import java.util.Objects;


/**
 * A block position together with the direction it faces.
 */
public record BlockCardinal(
  @JsonProperty(required = true) int x,
  @JsonProperty(required = true) int y,
  @JsonProperty(required = true) int z,
  @JsonProperty(required = true) Cardinal cardinal
) {
    public BlockCardinal {
        Objects.requireNonNull(cardinal, "cardinal");
    }

    public static BlockCardinal of(BlockPos pos, Cardinal cardinal) {
        return new BlockCardinal(pos.x(), pos.y(), pos.z(), cardinal);
    }


    //region Conversions
    public BlockPos pos() {
        return new BlockPos(x, y, z);
    }

    public Rotation rotation() {
        return Rotation.of(cardinal);
    }
    //endregion


    //region With
    public BlockCardinal flip() {
        return new BlockCardinal(x, y, z, cardinal.flip());
    }
    //endregion

}
