/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.attribute;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.data.AttributeData;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;

import java.util.UUID;


public class AttributeEffect implements EffectBehaviour {
    private final UUID uuid;

    private final Attribute attribute;
    private final AttributeModifier.Operation operation;
    private final float incrementPerLevel;


    private AttributeEffect(Builder builder) {
        this.uuid = UUID.randomUUID();

        this.attribute = builder.attribute;
        this.operation = builder.operation;
        this.incrementPerLevel = builder.incrementPerLevel;
    }


    public UUID uuid() {
        return uuid;
    }

    public Attribute attribute() {
        return attribute;
    }

    public AttributeModifier.Operation operation() {
        return operation;
    }

    public float incrementPerLevel() {
        return incrementPerLevel;
    }


    @Override
    public void apply(EffectInstance data) {
        AttributeData.builder(attribute)
          .key(Bits.key(uuid.toString()))
          .operation(operation)
          .value(data.modifier().amplifier() * incrementPerLevel)
          .build();
    }

    @Override
    public void unapply(EffectInstance data) {
        AttributeData.removeFrom(data.target(), attribute, Bits.key(uuid.toString()));
    }

    @Override
    public void tick(EffectInstance data, long tick) {
        // No-op
    }


    //region Builder
    public static Builder builder(Attribute attribute) {
        return new Builder(attribute);
    }

    public static final class Builder implements Buildable<AttributeEffect> {
        private final Attribute attribute;
        private AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
        private float incrementPerLevel = 1;

        private Builder(Attribute attribute) {
            this.attribute = attribute;
        }

        public Builder operation(AttributeModifier.Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder incrementPerLevel(float incrementPerLevel) {
            this.incrementPerLevel = incrementPerLevel;
            return this;
        }


        public Builder zero() {
            this.incrementPerLevel = -1;
            operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
            return this;
        }


        @Override
        public AttributeEffect build() {
            return new AttributeEffect(this);
        }

    }
    //endregion

}
