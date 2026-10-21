/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import com.fasterxml.jackson.annotation.JsonProperty;

import xyz.orbitary.bits.mc.world.Vec3;

import java.util.Objects;
import java.util.function.Predicate;


/**
 * Points inside {@code base} but not inside {@code cut}.
 */
public record Difference(
  @JsonProperty(required = true) Volume base,
  @JsonProperty(required = true) Volume cut
) implements Volume {
    public Difference {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(cut, "cut");
    }

    public static Difference of(Volume base, Volume cut) {
        return new Difference(base, cut);
    }

    @Override
    public boolean contains(Vec3 point) {
        return base.contains(point) && !cut.contains(point);
    }

    @Override
    public Predicate<Vec3> tester() {
        Predicate<Vec3> inBase = base.tester(), inCut = cut.tester();
        return point -> inBase.test(point) && !inCut.test(point);
    }

    /**
     * Removing {@code cut} can only shrink the shape, so the base bounds still enclose it.
     */
    @Override
    public Bounds bounds() {
        return base.bounds();
    }

}
