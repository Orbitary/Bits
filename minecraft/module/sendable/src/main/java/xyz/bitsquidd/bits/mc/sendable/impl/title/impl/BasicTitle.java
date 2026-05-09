/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.title.impl;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.title.AbstractTitle;


public final class BasicTitle extends AbstractTitle {
    private final Component title;
    private final Component subtitle;

    private BasicTitle(Builder builder) {
        this.title = builder.title;
        this.subtitle = builder.subtitle;
    }

    @Override
    public Component title(SendableState state) {
        return title;
    }

    @Override
    public Component subtitle(SendableState state) {
        return subtitle;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder implements Buildable<BasicTitle> {
        private Component title = Component.empty();
        private Component subtitle = Component.empty();

        private Builder() {}

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder subtitle(Component subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        @Override
        public BasicTitle build() {
            return new BasicTitle(this);
        }

    }


}
