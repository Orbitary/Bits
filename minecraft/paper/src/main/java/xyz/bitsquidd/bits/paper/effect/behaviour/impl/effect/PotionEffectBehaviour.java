/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;


// TODO:
//  Consider implementing a BukkitEffectRegistry - tracking Bukkit effects so we dont have to do a full remove(Type) (which may remove unrelated effects).
public final class PotionEffectBehaviour implements EffectBehaviour {
    private final PotionEffectType bukkitEffect;
    private final boolean hasIcon;

    private PotionEffectBehaviour(Builder builder) {
        this.bukkitEffect = builder.bukkitEffect;
        this.hasIcon = builder.hasIcon;
    }


    @Override
    public void apply(EffectInstance data) {
        data.target().addPotionEffect(new PotionEffect(
          bukkitEffect,
          Math.clamp(data.modifier().durationTicks(), -1, Integer.MAX_VALUE),
          Math.clamp(data.modifier().amplifier() - 1, 0, Integer.MAX_VALUE),
          false,
          false,
          hasIcon
        ));
    }

    @Override
    public void unapply(EffectInstance data) {
        data.target().removePotionEffect(bukkitEffect);
    }

    @Override
    public void tick(EffectInstance data, long tick) {
        // No-op
    }


    public static Builder builder(PotionEffectType bukkitEffect) {
        return new Builder(bukkitEffect);
    }

    public static final class Builder implements Buildable<PotionEffectBehaviour> {
        private final PotionEffectType bukkitEffect;
        private boolean hasIcon;


        private Builder(PotionEffectType bukkitEffect) {
            this.bukkitEffect = bukkitEffect;
        }


        public Builder icon() {
            this.hasIcon = true;
            return this;
        }


        @Override
        public PotionEffectBehaviour build() {
            return new PotionEffectBehaviour(this);
        }

    }


}
