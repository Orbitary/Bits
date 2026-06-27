/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour;


import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;


public interface EffectBehaviour {

    void apply(EffectInstance data);

    void unapply(EffectInstance data);


    void tick(EffectInstance data, long tick);

}
