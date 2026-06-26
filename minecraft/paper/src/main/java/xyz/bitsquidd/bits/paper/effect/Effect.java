/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.key.Key;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.ApiStatus;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.data.EffectData;
import xyz.bitsquidd.bits.paper.effect.data.EffectDataMap;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;
import xyz.bitsquidd.bits.paper.effect.state.EffectModifier;
import xyz.bitsquidd.bits.paper.effect.state.StateTransform;

import java.util.ArrayList;
import java.util.List;


public final class Effect {
    private final Key id;

    private final List<EffectBehaviour> behaviour;
    private final List<Effect> children;
    private final EffectDataMap data;
    private final StateTransform transform;


    private Effect(Builder builder) {
        this.id = builder.id;
        this.behaviour = ImmutableList.copyOf(builder.behaviour);
        this.children = ImmutableList.copyOf(builder.children);
        this.transform = builder.transform;

        this.data = builder.data;
        this.data.mergeFrom(children.stream().map(c -> c.data).toList());
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Effect other && other.id.equals(id);
    }


    public void apply(LivingEntity livingEntity, EffectModifier modifier) {
        EffectManager.INSTANCE.attemptApplyTo(livingEntity, this, modifier);
    }

    public void unapply(LivingEntity livingEntity) {
        EffectManager.INSTANCE.attemptUnapplyFrom(livingEntity, this);
    }


    @ApiStatus.Internal
    public void apply(EffectInstance instance) {
        EffectInstance transformed = instance.transform(transform);
        behaviour.forEach(b -> b.apply(transformed));
        children.forEach(c -> c.apply(transformed));

        EffectManager.INSTANCE.applyEffectInternal(instance);
    }

    @ApiStatus.Internal
    public void unapply(EffectInstance instance) {
        EffectInstance transformed = instance.transform(transform);
        behaviour.forEach(b -> b.unapply(transformed));
        children.forEach(c -> c.unapply(transformed));

        EffectManager.INSTANCE.unapplyEffectInternal(instance);
    }

    @ApiStatus.Internal
    public void tick(EffectInstance data, long tick) {
        EffectInstance transformed = data.transform(transform);
        behaviour.forEach(b -> b.tick(transformed, tick));
        children.forEach(c -> c.tick(transformed, tick));

        if (data.isExpired(tick)) unapply(transformed);
    }


    //region Builder
    public static Builder builder(Key id) {
        return new Builder(id);
    }

    public static final class Builder implements Buildable<Effect> {
        public final Key id;

        private final List<EffectBehaviour> behaviour = new ArrayList<>();
        private final List<Effect> children = new ArrayList<>();
        private final EffectDataMap data = new EffectDataMap();
        private StateTransform transform = StateTransform.identity();

        private Builder(Key id) {
            this.id = id;
        }


        public Builder with(EffectBehaviour behaviour) {
            this.behaviour.add(behaviour);
            return this;
        }

        public Builder with(Effect child) {
            this.children.add(child);
            return this;
        }

        public <D> Builder with(EffectData<D> key, D value) {
            this.data.put(key, value);
            return this;
        }

        public Builder with(StateTransform transform) {
            this.transform = transform;
            return this;
        }


        @Override
        public Effect build() {
            return new Effect(this);
        }

    }
    //endregion

}
