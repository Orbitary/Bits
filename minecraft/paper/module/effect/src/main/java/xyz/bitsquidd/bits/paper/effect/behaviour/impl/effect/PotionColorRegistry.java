/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.effect;

import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A registry for overriding the colors of Bukkit potion effects.
 *
 * @since 0.0.21
 */
public final class PotionColorRegistry {
    private PotionColorRegistry() {}

    private static final Map<PotionEffectType, Integer> colorOverrides = new ConcurrentHashMap<>();


    public static void setOverride(PotionEffectType effectType, int color) {
        colorOverrides.put(effectType, color);
    }


    public static Integer getEffectiveColor(PotionEffectType potionEffectType) {
        if (colorOverrides.containsKey(potionEffectType)) return colorOverrides.get(potionEffectType);
        return potionEffectType.getColor().asRGB();
    }

}
