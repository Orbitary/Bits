/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.effect.data.impl;

import org.bukkit.potion.PotionEffectTypeCategory;

import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.paper.effect.data.EffectData;

import java.util.List;
import java.util.Optional;


/**
 * An {@link EffectData} key for a category of the effect.
 *
 * @since 0.0.22
 */
public final class CategoryEffectData extends EffectData<PotionEffectTypeCategory> {
    public static final PotionEffectTypeCategory DEFAULT_CATEGORY = PotionEffectTypeCategory.NEUTRAL;

    CategoryEffectData() {
        super(Bits.key("effect_category"));
    }


    @Override
    public PotionEffectTypeCategory mergeStrategy(Optional<PotionEffectTypeCategory> parent, List<PotionEffectTypeCategory> children) {
        return parent.orElse(children.isEmpty() ? DEFAULT_CATEGORY : children.getFirst());
    }

}
