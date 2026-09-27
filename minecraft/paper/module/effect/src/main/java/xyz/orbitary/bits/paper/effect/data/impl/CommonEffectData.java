/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.effect.data.impl;


import xyz.orbitary.bits.paper.effect.data.EffectData;

/**
 * Built-in {@link EffectData} keys used by the effect system.
 *
 * @since 0.0.21
 */
public final class CommonEffectData {
    private CommonEffectData() {}

    /**
     * Packed ARGB color integer for the effect's visual representation.
     */
    public static final ColorEffectData COLOR = new ColorEffectData();

    /**
     * Display name {@link net.kyori.adventure.text.Component} for the effect.
     */
    public static final NameEffectData NAME = new NameEffectData();

    /**
     * Category of the effect, used for stating if its beneficial or not.
     */
    public static final CategoryEffectData CATEGORY = new CategoryEffectData();

}
