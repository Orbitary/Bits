/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.key.Key;

import xyz.orbitary.bits.mc.world.Coord;

import java.util.Objects;


public record WorldVolume(
  @JsonProperty(required = true) Key world,
  @JsonProperty(required = true) Volume volume
) {
    public WorldVolume {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(volume, "volume");
    }

    public static WorldVolume of(Key world, Volume volume) {
        return new WorldVolume(world, volume);
    }

    public boolean contains(Coord location) {
        return world.equals(location.world()) && volume.contains(location.pos().vec());
    }

    public Bounds bounds() {
        return volume.bounds();
    }

}
