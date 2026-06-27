/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.data;

import net.kyori.adventure.key.Key;

import java.util.List;
import java.util.Optional;


public abstract class EffectData<D> {
    private final Key id;

    protected EffectData(Key id) {
        this.id = id;
    }

    public static <D> EffectData<D> of(Key id) {
        return new EffectData<>(id) {};
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof EffectData<?> other && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public D mergeStrategy(Optional<D> parent, List<D> children) {
        return parent.orElseGet(children::getFirst); // There should be no situation where both parent and children are empty, but if it happens.
    }

}
