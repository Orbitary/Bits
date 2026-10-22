/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.util.math;

import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MathHelperTest {

    @Test
    void roundHalfUpAwayFromZero() {
        assertEquals(2.35, MathHelper.round(2.345, 2));
        assertEquals(-3.0, MathHelper.round(-2.5, 0));
    }

    @Test
    void roundRejectsNegativePlaces() {
        assertThrows(IllegalArgumentException.class, () -> MathHelper.round(1.0, -1));
    }

    @Test
    void sineAndCosineScaleByAmplitudeAndPeriod() {
        assertEquals(2.0, MathHelper.sin(4, 2, 1), 1e-9);
        assertEquals(2.0, MathHelper.cos(4, 2, 0), 1e-9);
        assertEquals(0.0, MathHelper.sin(4, 2, 0), 1e-9);
    }

    @Test
    void axisRotationIsReadBackFromQuaternion() {
        assertEquals(0.5f, MathHelper.Quaternion.getXRotation(new Quaternionf().rotationX(0.5f)), 1e-5f);
        assertEquals(0.5f, MathHelper.Quaternion.getYRotation(new Quaternionf().rotationY(0.5f)), 1e-5f);
        assertEquals(0.5f, MathHelper.Quaternion.getZRotation(new Quaternionf().rotationZ(0.5f)), 1e-5f);
    }

    @Test
    void invertNegatesTheRotation() {
        assertEquals(-0.5f, MathHelper.Quaternion.getXRotation(MathHelper.Quaternion.invertX(new Quaternionf().rotationX(0.5f))), 1e-5f);
    }

}
