/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.attribute;

import org.bukkit.attribute.Attribute;

import xyz.bitsquidd.bits.paper.effect.data.impl.ColorEffectData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A registry for providing default colors of Bukkit attributes.
 *
 * @since 0.0.21
 */
public final class AttributeColorRegistry {
    private AttributeColorRegistry() {}

    private static final Map<Attribute, Integer> colorOverrides = new ConcurrentHashMap<>();


    public static void setOverride(Attribute attribute, int color) {
        colorOverrides.put(attribute, color);
    }


    public static Integer getEffectiveColor(Attribute attribute) {
        if (colorOverrides.containsKey(attribute)) return colorOverrides.get(attribute);
        return ColorEffectData.DEFAULT_COLOR; // Default to white if no override is set
    }

}
