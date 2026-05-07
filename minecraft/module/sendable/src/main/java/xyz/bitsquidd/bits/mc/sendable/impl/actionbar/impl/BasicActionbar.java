/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.actionbar.impl;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.actionbar.AbstractActionbar;


public final class BasicActionbar extends AbstractActionbar {
    private final Component content;

    private BasicActionbar(Builder builder) {
        this.content = builder.content;
    }

    @Override
    public Component content(SendableState state) {
        return content;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder implements Buildable<BasicActionbar> {
        private Component content = Component.empty();

        private Builder() {}

        public Builder content(Component content) {
            this.content = content;
            return this;
        }

        @Override
        public BasicActionbar build() {
            return new BasicActionbar(this);
        }

    }


}
