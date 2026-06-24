/*
 * This file is part of the BiomeBattle project.
 *
 * Copyright (c) 2023-2026 BitSquidd - All Rights Reserved
 *
 * Unauthorized copying, distribution, or disclosure of this file, via any medium, is strictly prohibited.
 * Proprietary and confidential - for internal use only.
 */

package xyz.bitsquidd.bits.mc.util.noteblock;


public enum NoteBlockPitch {
    // Octave 1
    FS1(0, 0.5f, 0x59E800),
    G1(1, pitch(-11), 0x82CE00),
    GS1(2, pitch(-10), 0xACAC00),
    A1(3, pitch(-9), 0xCE8400),
    AS1(4, pitch(-8), 0xE85900),
    B1(5, pitch(-7), 0xF92E00),
    C1(6, pitch(-6), 0xFF0606),
    CS1(7, pitch(-5), 0xF9002E),
    D1(8, pitch(-4), 0xE80059),
    DS1(9, pitch(-3), 0xCE0082),
    E1(10, pitch(-2), 0xAC00AC),
    F1(11, pitch(-1), 0x8200CE),

    // Octave 2
    FS2(12, 1.0f, 0x5900E8),
    G2(13, pitch(1), 0x2E00F9),
    GS2(14, pitch(2), 0x0606FF),
    A2(15, pitch(3), 0x002EF9),
    AS2(16, pitch(4), 0x0059E8),
    B2(17, pitch(5), 0x0082CE),
    C2(18, pitch(6), 0x00ACAC),
    CS2(19, pitch(7), 0x00CE82),
    D2(20, pitch(8), 0x00E859),
    DS2(21, pitch(9), 0x00F92E),
    E2(22, pitch(10), 0x06FF06),
    F2(23, pitch(11), 0x2EF900),
    FS3(24, 2.0f, 0x59E800);

    public final int useCount;
    public final float pitch;
    public final int color;

    NoteBlockPitch(int useCount, float pitch, int color) {
        this.useCount = useCount;
        this.pitch = pitch;
        this.color = color;
    }

    private static float pitch(int semisFromMiddle) {
        return (float)Math.pow(2.0, semisFromMiddle / 12.0);
    }

    public NoteBlockPitch alt() {
        int ordinal = this.ordinal();
        if (ordinal < 12) {
            return values()[ordinal + 12];
        } else {
            return values()[ordinal - 12];
        }
    }

}
