/*
 * This file is part of the BiomeBattle project.
 *
 * Copyright (c) 2023-2026 BitSquidd - All Rights Reserved
 *
 * Unauthorized copying, distribution, or disclosure of this file, via any medium, is strictly prohibited.
 * Proprietary and confidential - for internal use only.
 */

package xyz.bitsquidd.bits.mc.util.noteblock;


import com.google.common.collect.ImmutableList;

import java.util.List;


public enum NoteBlockChord {
    C_MAJOR(NoteBlockPitch.C1, NoteBlockPitch.E1, NoteBlockPitch.G2),
    D_MINOR(NoteBlockPitch.D1, NoteBlockPitch.F1, NoteBlockPitch.A2),
    E_MINOR(NoteBlockPitch.E1, NoteBlockPitch.G2, NoteBlockPitch.B2),
    F_MAJOR(NoteBlockPitch.F1, NoteBlockPitch.A2, NoteBlockPitch.C2),
    G_MAJOR(NoteBlockPitch.G2, NoteBlockPitch.B2, NoteBlockPitch.D2),
    A_MINOR(NoteBlockPitch.A2, NoteBlockPitch.C2, NoteBlockPitch.E2),
    B_MINOR(NoteBlockPitch.B1, NoteBlockPitch.D1, NoteBlockPitch.FS2),
    D_MAJOR(NoteBlockPitch.D1, NoteBlockPitch.FS2, NoteBlockPitch.A2),
    BB_MAJOR(NoteBlockPitch.AS1, NoteBlockPitch.D1, NoteBlockPitch.F1),
    G_MINOR(NoteBlockPitch.G2, NoteBlockPitch.AS2, NoteBlockPitch.D2);

    public final List<NoteBlockPitch> pitches;

    NoteBlockChord(NoteBlockPitch... pitches) {
        this.pitches = ImmutableList.copyOf(pitches);
    }

}
