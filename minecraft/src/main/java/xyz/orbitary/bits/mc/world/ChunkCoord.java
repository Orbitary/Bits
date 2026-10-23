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

import java.util.Objects;


@JsonDeserialize(as = ChunkCoord.Record.class)
public interface ChunkCoord {

    Key world();

    ChunkPos pos();


    @With
    record Record(
      @JsonProperty(required = true) Key world,
      @JsonProperty(required = true) ChunkPos pos
    ) implements ChunkCoord {
        public Record {
            Objects.requireNonNull(world, "world");
            Objects.requireNonNull(pos, "pos");
        }

    }

}
