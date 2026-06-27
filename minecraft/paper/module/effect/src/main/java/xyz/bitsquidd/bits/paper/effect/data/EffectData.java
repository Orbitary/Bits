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


/**
 * A typed key for metadata that can be stored on an {@link xyz.bitsquidd.bits.paper.effect.Effect}
 * and retrieved via {@link xyz.bitsquidd.bits.paper.effect.Effect#getData(EffectData)}.
 * <p>
 * Subclass this to define custom metadata keys. Override {@link #mergeStrategy} to control
 * how parent and child values are combined when an effect hierarchy is built.
 *
 * @param <D> the type of value this key holds
 *
 * @since 0.0.21
 */
public abstract class EffectData<D> {
    private final Key id;

    protected EffectData(Key id) {
        this.id = id;
    }

    /**
     * Creates an anonymous {@code EffectData} key with the default merge strategy:
     * the parent value wins, falling back to the first child value if no parent is set.
     *
     * @param <D> the type of value this key holds
     * @param id  the unique identifier for this key
     * @return a new key
     *
     * @since 0.0.21
     */
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


    /**
     * Determines how the parent value and child values are combined when this effect's
     * data map is built.
     * <p>
     * The default implementation returns the parent value if present, or the first child value otherwise.
     *
     * @param parent   the value set directly on the parent effect, or empty if not set
     * @param children the values from each child effect, in registration order
     * @return the resolved value
     *
     * @since 0.0.21
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public D mergeStrategy(Optional<D> parent, List<D> children) {
        return parent.orElseGet(children::getFirst); // There should be no situation where both parent and children are empty, but if it happens.
    }

}
