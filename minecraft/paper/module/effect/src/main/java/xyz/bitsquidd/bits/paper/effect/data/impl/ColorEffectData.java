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


/**
 * An {@link EffectData} key for a packed ARGB color integer.
 * <p>
 * When parent and child values are merged, the parent color wins. If no parent color is set,
 * the child colors are blended via {@link xyz.bitsquidd.bits.util.color.Colors#mix}.
 *
 * @since 0.0.21
 */
public final class ColorEffectData extends EffectData<Integer> {
    public static final int DEFAULT_COLOR = 0xFFFFFF;

    ColorEffectData() {
        super(Bits.key("effect_color"));
    }


    @Override
    public Integer mergeStrategy(Optional<Integer> parent, List<Integer> children) {
        return parent.orElse(children.isEmpty() ? DEFAULT_COLOR : Colors.mix(children));
    }

}
