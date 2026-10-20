/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import java.util.ArrayList;
import java.util.List;


public final class VolumeUtils {
    private VolumeUtils() {}


    public static List<Volume> leaves(Volume volume) {
        List<Volume> leaves = new ArrayList<>();
        collectLeaves(volume, leaves);
        return leaves;
    }

    private static void collectLeaves(Volume volume, List<Volume> leaves) {
        switch (volume) {
            case Union union -> union.parts().forEach(part -> collectLeaves(part, leaves));
            case Difference difference -> collectLeaves(difference.base(), leaves);
            case Cuboid cuboid -> leaves.add(cuboid);
            case Cylinder cylinder -> leaves.add(cylinder);
            case Ellipsoid ellipsoid -> leaves.add(ellipsoid);
        }
    }

}
