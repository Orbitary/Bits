/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.behaviour.impl.attribute.AttributeColorRegistry;
import xyz.bitsquidd.bits.paper.effect.behaviour.impl.attribute.AttributeEffect;
import xyz.bitsquidd.bits.paper.effect.behaviour.impl.effect.PotionColorRegistry;
import xyz.bitsquidd.bits.paper.effect.behaviour.impl.effect.PotionEffect;
import xyz.bitsquidd.bits.paper.effect.data.EffectData;
import xyz.bitsquidd.bits.paper.effect.data.EffectDataMap;
import xyz.bitsquidd.bits.paper.effect.data.impl.CommonEffectData;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;
import xyz.bitsquidd.bits.paper.effect.state.EffectModifier;
import xyz.bitsquidd.bits.paper.effect.state.EffectTransform;
import xyz.bitsquidd.bits.wrapper.collection.pair.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


/**
 * An immutable definition of an effect that can be applied to living entities.
 * <p>
 * An effect is composed of {@link EffectBehaviour behaviours} that define what happens when it is
 * applied, removed, or ticked; optional child {@link Effect effects} that are applied alongside it;
 * typed {@link EffectData} metadata; and an {@link EffectTransform} that scales or overrides the
 * {@link EffectModifier} at the point of application.
 * <p>
 * Use {@link #builder(Key)} to construct instances, and {@link #apply(LivingEntity, EffectModifier)}
 * to apply an effect to an entity.
 *
 * @since 0.0.21
 */
public final class Effect {
    private final Key id;

    private final List<Pair<EffectBehaviour, EffectTransform>> behaviour;
    private final List<Pair<Effect, EffectTransform>> children;
    private final EffectDataMap data;


    private Effect(Builder builder) {
        this.id = builder.id;
        this.behaviour = ImmutableList.copyOf(builder.behaviour);
        this.children = ImmutableList.copyOf(builder.children);

        this.data = builder.data;
        this.data.mergeFrom(children.stream().map(c -> c.getFirst().data).toList());
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Effect other && other.id.equals(id);
    }


    /**
     * Returns the behaviours directly attached to this effect, executed in registration order.
     *
     * @return an unmodifiable list of behaviours
     *
     * @since 0.0.21
     */
    @Unmodifiable
    public List<Pair<EffectBehaviour, EffectTransform>> getBehaviour() {
        return behaviour;
    }

    /**
     * Returns the child effects composed into this effect.
     *
     * @return an unmodifiable list of children
     *
     * @since 0.0.21
     */
    @Unmodifiable
    public List<Pair<Effect, EffectTransform>> getChildren() {
        return children;
    }


    /**
     * Returns the resolved data value for the given key, merging this effect's value with
     * child values using the key's {@link EffectData#mergeStrategy merge strategy}.
     *
     * @param key the data key to look up
     *
     * @return the resolved value, or empty if not present in this effect or any child
     *
     * @since 0.0.21
     */
    public <D> Optional<D> getData(EffectData<D> key) {
        return data.get(key);
    }

    /**
     * Returns all raw (unmerged) data values for the given key from this effect and its direct children,
     * in registration order.
     *
     * @param key the data key to look up
     *
     * @return a list of raw values, or an empty list if none are present
     *
     * @since 0.0.21
     */
    public <D> List<D> getRawData(EffectData<D> key) {
        return data.getRaw(key);
    }


    /**
     * Registers and applies this effect to the given entity with the given modifier.
     * <p>
     * If this effect is already active on the entity, the existing instance is replaced.
     *
     * @param target   the entity to apply the effect to
     * @param modifier the modifier controlling amplifier and duration
     *
     * @since 0.0.21
     */
    public void apply(LivingEntity target, EffectModifier modifier) {
        apply(EffectManager.get().registerEffect(target, this, modifier));
    }

    public void apply(Collection<? extends LivingEntity> targets, EffectModifier modifier) {
        targets.forEach(target -> apply(target, modifier));
    }

    /**
     * Removes this effect from the given entity, invoking the unapply lifecycle on all
     * behaviours and children. Has no effect if the effect is not currently active on the entity.
     *
     * @param target the entity to remove the effect from
     *
     * @since 0.0.21
     */
    public void unapply(LivingEntity target) {
        EffectManager.get().getActiveEffect(target, this).ifPresent(instance -> {
            EffectManager.get().unregisterEffect(instance);
            unapply(instance);
        });
    }

    public void unapply(Collection<? extends LivingEntity> targets) {
        targets.forEach(this::unapply);
    }


    @ApiStatus.Internal
    void apply(EffectInstance instance) {
        behaviour.forEach(b -> b.getFirst().apply(instance.transform(b.getSecond())));
        children.forEach(c -> c.getFirst().apply(instance.transform(c.getSecond())));
    }

    @ApiStatus.Internal
    void unapply(EffectInstance instance) {
        behaviour.forEach(b -> b.getFirst().unapply(instance.transform(b.getSecond())));
        children.forEach(c -> c.getFirst().unapply(instance.transform(c.getSecond())));
    }

    @ApiStatus.Internal
    void tick(EffectInstance instance, long tick) {
        if (tick >= instance.endTick()) {
            if (instance.endTick() == tick) {
                EffectManager.get().unregisterEffect(instance); // Although we do not need to unregister child effects, we must unregister the parent its self.
                unapply(instance);
            }
            return;
        }

        behaviour.forEach(b -> b.getFirst().tick(instance.transform(b.getSecond()), tick));
        children.forEach(c -> c.getFirst().tick(instance.transform(c.getSecond()), tick));
    }


    //region Builder
    /**
     * An empty effect with no behaviours, children, or data.
     */
    public static final Effect EMPTY = builder(Key.key("empty")).build();

    /**
     * Creates a new builder for an effect with the given identifier.
     *
     * @param id the unique identifier for the effect
     *
     * @return a new builder
     *
     * @since 0.0.21
     */
    public static Builder builder(Key id) {
        return new Builder(id);
    }

    /**
     * Utility builder with arbitrary id: used for chaining.
     *
     * @return a new builder with an arbitrary id
     *
     * @since 0.0.21
     */
    public static Builder inner() {
        return new Builder(Bits.key("."));
    }


    /**
     * A fluent builder for constructing {@link Effect} instances.
     * <p>
     * Behaviours, children, data, and a transform can be composed before calling {@link #build()}.
     *
     * @since 0.0.21
     */
    public static final class Builder implements Buildable<Effect> {
        public final Key id;

        private final List<Pair<EffectBehaviour, EffectTransform>> behaviour = new ArrayList<>();
        private final List<Pair<Effect, EffectTransform>> children = new ArrayList<>();
        private final EffectDataMap data = new EffectDataMap();

        private final EffectDataMap convenience = new EffectDataMap();

        private Builder(Key id) {
            this.id = id;
        }


        public Builder with(EffectBehaviour behaviour, EffectTransform transform) {
            this.behaviour.add(Pair.immutable(behaviour, transform));

            // Convenience functionality: Most of the time simple effects will want to have their name and color automatically set in the data map.
            // Setting data() at any point in the builder lifecycle will override these defaults.
            switch (behaviour) {
                case PotionEffect potionEffect -> {
                    convenience.merge(CommonEffectData.NAME, Component.translatable(potionEffect.bukkitEffect().translationKey()));
                    convenience.merge(CommonEffectData.COLOR, PotionColorRegistry.getEffectiveColor(potionEffect.bukkitEffect()));
                }
                case AttributeEffect attributeEffect -> {
                    convenience.merge(CommonEffectData.NAME, Component.translatable(attributeEffect.attribute().translationKey()));
                    convenience.merge(CommonEffectData.COLOR, AttributeColorRegistry.getEffectiveColor(attributeEffect.attribute()));
                }
                default -> {}
            }

            return this;
        }

        public Builder with(EffectBehaviour behaviour) {
            return with(behaviour, EffectTransform.identity());
        }

        public Builder with(Supplier<? extends EffectBehaviour> behaviour, EffectTransform transform) {
            return with(behaviour.get(), transform);
        }

        public Builder with(Supplier<? extends EffectBehaviour> behaviour) {
            return with(behaviour.get());
        }


        public Builder child(Effect child, EffectTransform transform) {
            this.children.add(Pair.immutable(child, transform));
            return this;
        }

        public Builder child(Effect child) {
            return child(child, EffectTransform.identity());
        }

        public Builder child(Supplier<? extends Effect> child, EffectTransform transform) {
            return child(child.get(), transform);
        }

        public Builder child(Supplier<? extends Effect> child) {
            return child(child.get());
        }


        public <D> Builder data(EffectData<D> key, D value) {
            this.data.put(key, value);
            return this;
        }


        @Override
        public Effect build() {
            data.mergeFrom(List.of(convenience));
            return new Effect(this);
        }

    }
    //endregion

}
