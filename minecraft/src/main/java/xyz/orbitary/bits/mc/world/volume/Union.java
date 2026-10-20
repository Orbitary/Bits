/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import com.fasterxml.jackson.annotation.JsonProperty;

import xyz.orbitary.bits.mc.world.Vec3;

import java.util.ArrayList;
import java.util.List;


/**
 * Every point inside any of {@code parts}. Overlapping parts are allowed.
 */
public record Union(
  @JsonProperty(required = true) List<Volume> parts
) implements Volume {
    public Union {
        parts = List.copyOf(parts);
        if (parts.isEmpty()) throw new IllegalArgumentException("Union needs at least one part");
    }

    @Override
    public boolean contains(Vec3 point) {
        for (Volume part : parts) {
            if (part.contains(point)) return true;
        }
        return false;
    }

    @Override
    public Bounds bounds() {
        Bounds merged = parts.getFirst().bounds();
        for (int i = 1; i < parts.size(); i++) {
            merged = merged.merge(parts.get(i).bounds());
        }
        return merged;
    }

    @Override
    public Volume union(Volume other) {
        return new Union(new ArrayList<>(parts) {{
            add(other);
        }});
    }

}
