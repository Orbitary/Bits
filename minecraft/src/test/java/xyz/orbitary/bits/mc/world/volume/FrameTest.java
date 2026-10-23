/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

import xyz.orbitary.bits.mc.world.Vec3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


class FrameTest {
    @Test
    void identityFrameTranslatesPointsAndPreservesShapeExtents() {
        Vec3 center = new Vec3(1, 2, 3);
        Frame frame = new Frame(center, new Quaternionf());
        assertSame(center, frame.center());
        assertEquals(new Vec3(3, 4, 5), frame.toLocal(new Vec3(4, 6, 8)));

        for (int axis = 0; axis < 3; axis++) {
            double expected = axis + 2;
            assertEquals(expected, frame.boxHalfExtent(axis, 2, 3, 4));
            assertEquals(expected, frame.ellipsoidHalfExtent(axis, 2, 3, 4));
        }
        assertEquals(2, frame.cylinderHalfExtent(0, 2, 3, 4));
        assertEquals(4, frame.cylinderHalfExtent(1, 2, 3, 4));
        assertEquals(3, frame.cylinderHalfExtent(2, 2, 3, 4));
    }

    @Test
    void rotatedFrameUsesTheInverseTransformAndRotatesBounds() {
        Frame frame = new Frame(Vec3.ZERO, new Quaternionf().rotateZ((float)(Math.PI / 2)));
        Vec3 local = frame.toLocal(new Vec3(1, 0, 0));
        assertEquals(0, local.x(), 1e-6);
        assertEquals(-1, local.y(), 1e-6);
        assertEquals(0, local.z(), 1e-6);
        assertEquals(3, frame.boxHalfExtent(0, 2, 3, 4), 1e-6);
        assertEquals(2, frame.boxHalfExtent(1, 2, 3, 4), 1e-6);
        assertEquals(4, frame.boxHalfExtent(2, 2, 3, 4), 1e-6);
        assertEquals(3, frame.ellipsoidHalfExtent(0, 2, 3, 4), 1e-6);
        assertEquals(4, frame.cylinderHalfExtent(0, 2, 3, 4), 1e-6);
    }

    @Test
    void frameKeepsItsRotationSnapshot() {
        Quaternionf rotation = new Quaternionf();
        Frame frame = new Frame(Vec3.ZERO, rotation);
        rotation.rotateZ((float)(Math.PI / 2));
        assertEquals(new Vec3(1, 0, 0), frame.toLocal(new Vec3(1, 0, 0)));
    }
}
