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

import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.data.EffectData;
import xyz.bitsquidd.bits.paper.effect.data.EffectDataMap;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;
import xyz.bitsquidd.bits.paper.effect.state.EffectModifier;
import xyz.bitsquidd.bits.paper.effect.state.EffectTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;


public final class Effect {
    private final Key id;

    private final List<EffectBehaviour> behaviour;
    private final List<Effect> children;
    private final EffectDataMap data;
    private final EffectTransform transform;


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


    @Unmodifiable
    public List<EffectBehaviour> getBehaviour() {
        return behaviour;
    }

    public List<Effect> getChildren() {
        return children;
    }


    public <D> Optional<D> getData(EffectData<D> key) {
        return data.get(key);
    }

    public <D> List<D> getChildData(EffectData<D> key) {
        return data.getRaw(key);
    }

    public <D> List<D> getRawData(EffectData<D> key) {
        return data.getRaw(key);
    }


    public int getDuration(EffectModifier modifier) {
        return transform.transform(modifier).durationTicks();
    }

    public int getAmplifier(EffectModifier modifier) {
        return transform.transform(modifier).amplifier();
    }


    public void apply(LivingEntity livingEntity, EffectModifier modifier) {
        apply(EffectManager.INSTANCE.registerEffect(livingEntity, this, modifier));
    }

    public void unapply(LivingEntity livingEntity) {
        EffectManager.INSTANCE.getActiveEffect(livingEntity, this).ifPresent(instance -> {
            EffectManager.INSTANCE.unregisterEffect(instance);
            unapply(instance);
        });
    }


    @ApiStatus.Internal
    void apply(EffectInstance instance) {
        EffectInstance transformed = instance.transform(transform);
        behaviour.forEach(b -> b.apply(transformed));
        children.forEach(c -> c.apply(transformed));
    }

    @ApiStatus.Internal
    void unapply(EffectInstance instance) {
        EffectInstance transformed = instance.transform(transform);
        behaviour.forEach(b -> b.unapply(transformed));
        children.forEach(c -> c.unapply(transformed));
    }

    @ApiStatus.Internal
    void tick(EffectInstance data, long tick) {
        EffectInstance transformed = data.transform(transform);

        if (tick >= transformed.endTick()) {
            if (transformed.endTick() == tick) unapply(data); // Dont use transformed, it is already transformed in apply.
            return;
        }

        behaviour.forEach(b -> b.tick(transformed, tick));
        children.forEach(c -> c.tick(transformed, tick));
    }


    //region Builder
    public static final Effect EMPTY = builder(Key.key("empty")).build();

    public static Builder builder(Key id) {
        return new Builder(id);
    }

    private static Builder builderInternal() {
        return new Builder(Bits.key(UUID.randomUUID().toString()));
    }

    public Builder toBuilder() {
        Builder builder = new Builder(id);
        builder.behaviour.addAll(behaviour);
        builder.children.addAll(children);
        builder.data.mergeFrom(List.of(data));
        builder.transform = transform;
        return builder;
    }

    public static final class Builder implements Buildable<Effect> {
        public final Key id;

        private final List<EffectBehaviour> behaviour = new ArrayList<>();
        private final List<Effect> children = new ArrayList<>();
        private final EffectDataMap data = new EffectDataMap();
        private EffectTransform transform = EffectTransform.identity();

        private Builder(Key id) {
            this.id = id;
        }


        public Builder with(EffectBehaviour behaviour) {
            this.behaviour.add(behaviour);
            return this;
        }

        public Builder with(Supplier<? extends EffectBehaviour> behaviour) {
            this.behaviour.add(behaviour.get());
            return this;
        }


        public Builder with(EffectBehaviour behaviour, EffectTransform transform) {
            return child(builderInternal()
              .with(behaviour)
              .transform(transform)
            );
        }

        public Builder with(Supplier<? extends EffectBehaviour> behaviour, EffectTransform transform) {
            return child(builderInternal()
              .with(behaviour.get())
              .transform(transform)
            );
        }


        public Builder child(Effect child) {
            this.children.add(child);
            return this;
        }

        public Builder child(Effect child, EffectTransform transform) {
            this.children.add(child.toBuilder().transform(child.transform.andThen(transform)).build());
            return this;
        }

        public Builder child(Supplier<? extends Effect> child) {
            this.children.add(child.get());
            return this;
        }

        public Builder child(Supplier<? extends Effect> child, EffectTransform transform) {
            Effect built = child.get();
            this.children.add(built.toBuilder().transform(built.transform.andThen(transform)).build());
            return this;
        }

        public <D> Builder data(EffectData<D> key, D value) {
            this.data.put(key, value);
            return this;
        }

        public Builder transform(EffectTransform transform) {
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
