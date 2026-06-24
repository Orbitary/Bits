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


public enum NoteBlockProgression {
    C_POP(NoteBlockChord.C_MAJOR, NoteBlockChord.G_MAJOR, NoteBlockChord.A_MINOR, NoteBlockChord.F_MAJOR),
    C_FIFTIES(NoteBlockChord.C_MAJOR, NoteBlockChord.A_MINOR, NoteBlockChord.F_MAJOR, NoteBlockChord.G_MAJOR),
    C_JAZZ(NoteBlockChord.D_MINOR, NoteBlockChord.G_MAJOR, NoteBlockChord.C_MAJOR),
    C_EMOTIONAL(NoteBlockChord.A_MINOR, NoteBlockChord.F_MAJOR, NoteBlockChord.C_MAJOR, NoteBlockChord.G_MAJOR),

    G_POP(NoteBlockChord.G_MAJOR, NoteBlockChord.D_MAJOR, NoteBlockChord.E_MINOR, NoteBlockChord.C_MAJOR),
    G_BALLAD(NoteBlockChord.E_MINOR, NoteBlockChord.C_MAJOR, NoteBlockChord.G_MAJOR, NoteBlockChord.D_MAJOR),
    G_JAZZ(NoteBlockChord.A_MINOR, NoteBlockChord.D_MAJOR, NoteBlockChord.G_MAJOR),

    F_POP(NoteBlockChord.F_MAJOR, NoteBlockChord.C_MAJOR, NoteBlockChord.D_MINOR, NoteBlockChord.BB_MAJOR),
    F_BALLAD(NoteBlockChord.D_MINOR, NoteBlockChord.BB_MAJOR, NoteBlockChord.F_MAJOR, NoteBlockChord.C_MAJOR),
    F_JAZZ(NoteBlockChord.G_MINOR, NoteBlockChord.C_MAJOR, NoteBlockChord.F_MAJOR);

    public final List<NoteBlockChord> chords;

    NoteBlockProgression(NoteBlockChord... chords) {
        this.chords = ImmutableList.copyOf(chords);
    }
}
