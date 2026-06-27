/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.effect;

import org.bukkit.potion.PotionEffectType;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;


// TODO:
//  Consider implementing a BukkitEffectRegistry - tracking Bukkit effects so we dont have to do a full remove(Type) (which may remove unrelated effects).

/**
 * An {@link EffectBehaviour} that applies a vanilla Bukkit potion effect to the target entity.
 * <p>
 * The {@link xyz.bitsquidd.bits.paper.effect.state.EffectModifier} amplifier and duration are
 * mapped to the corresponding Bukkit parameters on apply and removed on unapply.
 *
 * @since 0.0.21
 */
public final class PotionEffect implements EffectBehaviour {
    private final PotionEffectType bukkitEffect;
    private final boolean hasIcon;

    private PotionEffect(Builder builder) {
        this.bukkitEffect = builder.bukkitEffect;
        this.hasIcon = builder.hasIcon;
    }


    public PotionEffectType bukkitEffect() {
        return bukkitEffect;
    }

    public boolean hasIcon() {
        return hasIcon;
    }


    @Override
    public void apply(EffectInstance data) {
        data.target().addPotionEffect(new org.bukkit.potion.PotionEffect(
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

    public static final class Builder implements Buildable<PotionEffect> {
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
        public PotionEffect build() {
            return new PotionEffect(this);
        }

    }


}
