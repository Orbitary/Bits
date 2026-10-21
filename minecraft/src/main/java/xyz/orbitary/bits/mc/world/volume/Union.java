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
import java.util.function.Predicate;


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

    public static Union of(Volume... parts) {
        return new Union(List.of(parts));
    }

    @Override
    public boolean contains(Vec3 point) {
        for (Volume part : parts) {
            if (part.contains(point)) return true;
        }
        return false;
    }

    @Override
    public Predicate<Vec3> tester() {
        List<Predicate<Vec3>> testers = parts.stream().map(Volume::tester).toList();
        return point -> {
            for (Predicate<Vec3> test : testers) {
                if (test.test(point)) return true;
            }
            return false;
        };
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
        List<Volume> all = new ArrayList<>(parts);
        all.add(other);
        return new Union(all);
    }

}
