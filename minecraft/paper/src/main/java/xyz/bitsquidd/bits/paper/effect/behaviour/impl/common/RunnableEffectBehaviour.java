/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.effect.behaviour.impl.common;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.effect.behaviour.EffectBehaviour;
import xyz.bitsquidd.bits.paper.effect.state.EffectInstance;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class RunnableEffectBehaviour implements EffectBehaviour {
    private final Consumer<EffectInstance> onApply;
    private final Consumer<EffectInstance> onUnapply;
    private final BiConsumer<EffectInstance, Integer> onTick;

    private RunnableEffectBehaviour(Builder builder) {
        this.onApply = builder.onApply;
        this.onUnapply = builder.onUnapply;
        this.onTick = builder.onTick;
    }


    @Override
    public void apply(EffectInstance data) {
        onApply.accept(data);
    }

    @Override
    public void unapply(EffectInstance data) {
        onUnapply.accept(data);
    }

    @Override
    public void tick(EffectInstance data, long tick) {
        onTick.accept(data, tick);
    }


    //region Builder
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Buildable<RunnableEffectBehaviour> {
        private Consumer<EffectInstance> onApply = _ -> {};
        private Consumer<EffectInstance> onUnapply = _ -> {};
        private BiConsumer<EffectInstance, Integer> onTick = (_, _) -> {};

        private Builder() {}

        public Builder onApply(Consumer<EffectInstance> onApply) {
            Consumer<EffectInstance> copy = this.onApply;
            this.onApply = (data) -> {
                copy.accept(data);
                onApply.accept(data);
            };

            return this;
        }

        public Builder onUnapply(Consumer<EffectInstance> onUnapply) {
            Consumer<EffectInstance> copy = this.onUnapply;
            this.onUnapply = (data) -> {
                copy.accept(data);
                onUnapply.accept(data);
            };
            return this;
        }

        public Builder onTick(BiConsumer<EffectInstance, Integer> onTick) {
            BiConsumer<EffectInstance, Integer> copy = this.onTick;
            this.onTick = (data, tick) -> {
                copy.accept(data, tick);
                onTick.accept(data, tick);
            };
            return this;
        }

        @Override
        public RunnableEffectBehaviour build() {
            return new RunnableEffectBehaviour(this);
        }

    }
    //endregion


}
