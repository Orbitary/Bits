/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class VolumeUtils {
    private VolumeUtils() {}


    public static List<Volume> leaves(Volume volume) {
        List<Volume> leaves = new ArrayList<>();
        collectLeaves(volume, leaves);
        return leaves;
    }

    static void requirePositive(String name, double... values) {
        for (double value : values) {
            if (!(value > 0)) throw new IllegalArgumentException(name + " must be positive: " + Arrays.toString(values));
        }
    }

    static Quaternionf requireRotation(Quaternionf rotation) {
        double lengthSquared = rotation.lengthSquared();
        if (!(Double.isFinite(lengthSquared) && lengthSquared > 0)) {
            throw new IllegalArgumentException("rotation must be a non-zero finite quaternion: " + rotation);
        }
        return new Quaternionf(rotation);
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
