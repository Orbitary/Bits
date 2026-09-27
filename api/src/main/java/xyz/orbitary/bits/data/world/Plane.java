/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.data.world;

import java.util.Set;

/**
 * Represents the three planes in a 3D space, each defined by a pair of axes.
 *
 * @since 0.0.12
 */
public enum Plane {
    XY(Set.of(Axis.X, Axis.Y)),
    XZ(Set.of(Axis.X, Axis.Z)),
    YZ(Set.of(Axis.Y, Axis.Z)),
    ;

    public final Set<Axis> axes;

    Plane(Set<Axis> axes) {
        this.axes = axes;
    }
}
