/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour;


import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;


/**
 * Defines the lifecycle callbacks for an effect.
 * <p>
 * Implementations are invoked when an effect is applied, when it is removed, and on each
 * server tick while it is active.
 *
 * @since 0.0.21
 */
public interface EffectBehaviour {

    /**
     * Invoked when the effect is applied to an entity.
     *
     * @param data the active effect instance
     *
     * @since 0.0.21
     */
    void apply(EffectInstance data);

    /**
     * Invoked when the effect is removed from an entity, either by expiry or manual removal.
     *
     * @param data the active effect instance
     *
     * @since 0.0.21
     */
    void unapply(EffectInstance data);

    /**
     * Invoked on every server tick while the effect is active.
     *
     * @param data the active effect instance
     * @param tick the current server tick
     *
     * @since 0.0.21
     */
    void tick(EffectInstance data, long tick);

}
