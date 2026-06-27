/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.data.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.paper.effect.data.EffectData;

import java.util.List;
import java.util.Optional;


/**
 * An {@link EffectData} key for a display name {@link Component}.
 * <p>
 * When parent and child values are merged, the parent name wins. If no parent name is set,
 * child names are joined with {@code " & "}.
 *
 * @since 0.0.21
 */
public final class NameEffectData extends EffectData<Component> {
    NameEffectData() {
        super(Bits.key("effect_name"));
    }


    @Override
    public Component mergeStrategy(Optional<Component> parent, List<Component> children) {
        return parent.orElseGet(() ->
          Component.join(JoinConfiguration.separator(Component.text(" & ")), children)
        );
    }

}
