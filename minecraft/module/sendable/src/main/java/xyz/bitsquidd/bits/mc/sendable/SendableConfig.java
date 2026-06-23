/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import xyz.bitsquidd.bits.lifecycle.builder.ExtendableBuildable;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;


public abstract class SendableConfig {
    protected final long tickRate;              // How often the tick is incremented. <=0 for never.
    protected final long maxTicks;              // How many (global) ticks until the sendable is removed.

    private final int priority;                 // The priority of the sendable. Higher priority = replace / overlap lower priority ones.
    private final @Nullable SendableFilter<Sendable> replaces; // A predicate to determine whether this sendable should replace another sendable.

    private final boolean reverseOnExpire;      // Whether the sendable should reverse when it reaches max ticks.
    private final boolean persistent;           // Whether the sendable should persist (e.g. through player respawns, world changing, disconnect etc.)

    protected SendableConfig(Builder<?> builder) {
        this.tickRate = builder.tickRate;
        this.maxTicks = builder.maxTicks;
        this.priority = builder.priority;
        this.replaces = builder.replaces;
        this.reverseOnExpire = builder.reverseOnExpire;
        this.persistent = builder.persistent;
    }


    public long tickRate() {
        return tickRate;
    }

    public long maxTicks() {
        return maxTicks;
    }

    public int priority() {
        return priority;
    }

    public @Nullable SendableFilter<Sendable> replaces() {
        return replaces;
    }

    public boolean reverseOnExpire() {
        return reverseOnExpire;
    }

    public boolean persistent() {
        return persistent;
    }


    public abstract static class Builder<B extends Builder<B>> extends ExtendableBuildable<SendableConfig, B> {
        private long tickRate = Sendable.TICK_RATE_DEFAULT;
        private long maxTicks = Sendable.MAX_TICKS;

        private int priority = 0;
        private @Nullable SendableFilter<Sendable> replaces;

        private boolean reverseOnExpire = false;
        private boolean persistent = false;


        protected Builder() {}

        public B tickRate(@Range(from = 0, to = Sendable.MAX_TICKS) long tickRate) {
            this.tickRate = Math.clamp(tickRate, 0, Sendable.MAX_TICKS);
            return self();
        }

        public B maxTicks(@Range(from = 0, to = Sendable.MAX_TICKS) long maxTicks) {
            this.maxTicks = Math.clamp(maxTicks, 0, Sendable.MAX_TICKS);
            return self();
        }

        public B priority(int priority) {
            this.priority = priority;
            return self();
        }

        public B replaces(SendableFilter<Sendable> replaces) {
            this.replaces = replaces;
            return self();
        }

        public B reverseOnExpire() {
            this.reverseOnExpire = true;
            return self();
        }

        public B reverseOnExpire(boolean reverseOnExpire) {
            this.reverseOnExpire = reverseOnExpire;
            return self();
        }

        public B persistent() {
            this.persistent = true;
            return self();
        }

        public B persistent(boolean persistent) {
            this.persistent = persistent;
            return self();
        }

    }

}
