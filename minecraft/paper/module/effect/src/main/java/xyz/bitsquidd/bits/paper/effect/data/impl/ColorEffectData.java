/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.data.impl;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.paper.effect.data.EffectData;
import xyz.bitsquidd.bits.util.color.Colors;

import java.util.List;
import java.util.Optional;


public final class ColorEffectData extends EffectData<Integer> {


    ColorEffectData() {
        super(Bits.key("effect_color"));
    }


    @Override
    public Integer mergeStrategy(Optional<Integer> parent, List<Integer> children) {
        return parent.orElse(Colors.mix(children));
    }

}
