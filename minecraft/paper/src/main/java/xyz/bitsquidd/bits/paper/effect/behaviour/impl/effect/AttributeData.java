/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.effect;

import com.google.common.collect.Multimap;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.log.Logger;

import java.util.UUID;

import static xyz.bitsquidd.bits.paper.util.Keys.NSK;


public final class AttributeData {
    private final Attribute attribute;
    private final double value;
    private final AttributeModifier.Operation operation;

    private final NamespacedKey key;

    private AttributeData(Builder builder) {
        this.attribute = builder.attribute;
        this.value = builder.value;
        this.operation = builder.operation;

        this.key = NSK(builder.key != null ? builder.key : Bits.key(UUID.randomUUID().toString()));
    }


    public void applyTo(ItemMeta meta, EquipmentSlotGroup slots) {
        Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
        if (modifiers != null) {
            if (modifiers.values().stream().anyMatch(modifier -> modifier.getKey().equals(key))) {
                Logger.warn("Attribute modifier with key " + key + " already exists on item meta. Skipping addition.");
                return;
            }
        }

        meta.addAttributeModifier(
          attribute,
          new AttributeModifier(
            key,
            value,
            operation,
            slots
          )
        );
    }

    public void applyTo(Attributable attributable) {
        AttributeInstance attributeInstance = attributable.getAttribute(attribute);

        if (attributeInstance != null) {
            attributeInstance.removeModifier(key);
            attributeInstance.addTransientModifier(new AttributeModifier(key, value, operation));
        } else {
            attributable.registerAttribute(attribute);
            AttributeInstance newAttributeInstance = attributable.getAttribute(attribute);

            if (newAttributeInstance != null) {
                newAttributeInstance.addTransientModifier(new AttributeModifier(key, value, operation));
            } else {
                Logger.warn("Failed to register attribute " + attribute + " for attributable " + attributable + ". Skipping addition of modifier.");
            }
        }
    }


    public void removeFrom(Attributable attributable) {
        AttributeData.removeFrom(attributable, attribute, key);
    }


    //region Static utilities
    public static void clear(Attributable attributable) {
        Registry.ATTRIBUTE.stream().forEach(attr -> {
            AttributeInstance instance = attributable.getAttribute(attr);
            if (instance != null) instance.getModifiers().forEach(instance::removeModifier);
        });
    }

    public static void removeFrom(Attributable attributable, Attribute attribute, Key key) {
        AttributeInstance attributeInstance = attributable.getAttribute(attribute);
        if (attributeInstance == null) return;

        attributeInstance.removeModifier(key);
    }

    public static double getValue(Attributable attributable, Attribute attribute) {
        AttributeInstance attributeAttribute = attributable.getAttribute(attribute);
        double value = 1;
        if (attributeAttribute != null) value = attributeAttribute.getValue();

        return value;
    }
    //endregion


    //region Builder
    public static Builder builder(Attribute attribute) {
        return new Builder(attribute);
    }

    public static final class Builder implements Buildable<AttributeData> {
        private final Attribute attribute;
        private double value = 1;
        private AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
        private @Nullable Key key = null;

        private Builder(Attribute attribute) {
            this.attribute = attribute;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public Builder operation(AttributeModifier.Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder key(Key key) {
            this.key = key;
            return this;
        }

        @Override
        public AttributeData build() {
            return new AttributeData(this);
        }

    }
    //endregion

}
