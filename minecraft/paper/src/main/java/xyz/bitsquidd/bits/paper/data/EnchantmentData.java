/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.data;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;


public final class EnchantmentData {
    private final Enchantment enchantment;         // The enchantment to be applied to the item.
    private final int level;                       // The level of the enchantment.
    private final boolean ignoreLevelRestriction;  // Whether to ignore the level restriction when applying the enchantment.
    private final boolean hidden;                  // Flag for custom implementations: Whether the enchantment should be hidden from the item's lore or not.


    private EnchantmentData(Builder builder) {
        this.enchantment = builder.enchantment;
        this.level = builder.level;
        this.ignoreLevelRestriction = builder.ignoreLevelRestriction;
        this.hidden = builder.hidden;
    }


    public Enchantment enchantment() {
        return enchantment;
    }

    public int level() {
        return level;
    }

    public boolean ignoreLevelRestriction() {
        return ignoreLevelRestriction;
    }

    public boolean hidden() {
        return hidden;
    }


    public void applyTo(ItemMeta meta) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
    }

    public void applyTo(ItemStack itemStack) {
        itemStack.editMeta(this::applyTo);
    }


    //region Builder
    public static Builder builder(Enchantment enchantment) {
        return new Builder(enchantment);
    }

    public static EnchantmentData basic(Enchantment enchantment, int level) {
        return builder(enchantment).level(level).build();
    }


    public static class Builder implements Buildable<EnchantmentData> {
        private final Enchantment enchantment;
        private int level = 1;
        private boolean ignoreLevelRestriction = true;
        private boolean hidden = false;

        private Builder(Enchantment enchantment) {
            this.enchantment = enchantment;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder ignoreLevelRestriction(boolean ignoreLevelRestriction) {
            this.ignoreLevelRestriction = ignoreLevelRestriction;
            return this;
        }

        public Builder hidden() {
            this.hidden = true;
            return this;
        }

        @Override
        public EnchantmentData build() {
            return new EnchantmentData(this);
        }

    }
    //endregion


}


