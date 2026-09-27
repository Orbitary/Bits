/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.data.world;

/**
 * Represents the four whole block (clockwise) rotations in a plane (0*, 90*, 180*, and 270*).
 *
 * @since 0.0.12
 */
public enum WholeRot {
    R0(0),
    R90(90),
    R180(180),
    R270(270),
    ;

    private final int degrees;

    WholeRot(int degrees) {
        this.degrees = degrees;
    }

    public int getDegrees() {
        return degrees;
    }

    public static WholeRot from(int degrees) {
        return switch (((degrees % 360) + 360) % 360) {
            case 0 -> R0;
            case 90 -> R90;
            case 180 -> R180;
            case 270 -> R270;
            default -> throw new IllegalArgumentException("Invalid degrees: " + degrees);
        };
    }

}
