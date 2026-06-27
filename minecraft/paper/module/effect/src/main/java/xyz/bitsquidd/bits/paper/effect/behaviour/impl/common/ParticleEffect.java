/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.common;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.BoundingBox;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.data.impl.CommonEffectData;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;
import xyz.bitsquidd.bits.util.Randomness;
import xyz.bitsquidd.bits.util.color.Colors;

import java.util.List;


// TODO generic particle effects to allow stuff like flames.

/**
 * An {@link EffectBehaviour} that spawns {@link org.bukkit.Particle#ENTITY_EFFECT} particles
 * around the target entity on each tick.
 * <p>
 * Particle color is sampled randomly from the effect's {@link CommonEffectData#COLOR} data;
 * defaults to white ({@code 0xFFFFFF}) if none is set.
 *
 * @since 0.0.21
 */
public class ParticleEffect implements EffectBehaviour {
    private final boolean ambient;

    public ParticleEffect(Builder builder) {
        this.ambient = builder.ambient;
    }

    @Override
    public void apply(EffectInstance data) {
        //No-op
    }

    @Override
    public void unapply(EffectInstance data) {
        //No-op
    }

    @Override
    public void tick(EffectInstance data, long tick) {
        List<Integer> colors = data.effect().getRawData(CommonEffectData.COLOR);
        if (colors.isEmpty()) colors.add(0xFFFFFF);

        int color = Randomness.getRandomElement(colors);
        Color affectedColor = Color.fromARGB(ambient ? 127 : 255, Colors.red(color), Colors.green(color), Colors.blue(color));

        BoundingBox boundingBox = data.target().getBoundingBox();
        Location center = boundingBox.getCenter().toLocation(data.target().getWorld());

        Particle.ENTITY_EFFECT.builder()
          .location(center)
          .offset(boundingBox.getWidthX() * 0.5, boundingBox.getHeight() * 0.5, boundingBox.getWidthZ() * 0.5)
          .data(affectedColor)
          .count(1)
          .spawn();
    }


    //region Builder
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Buildable<ParticleEffect> {
        private boolean ambient = true;

        private Builder() {}

        public Builder ambient(boolean ambient) {
            this.ambient = ambient;
            return this;
        }


        @Override
        public ParticleEffect build() {
            return new ParticleEffect(this);
        }

    }
    //endregion


}
