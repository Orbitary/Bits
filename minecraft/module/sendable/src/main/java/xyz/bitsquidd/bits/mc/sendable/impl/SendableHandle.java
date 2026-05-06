/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableConfig;
import xyz.bitsquidd.bits.wrapper.Percentage;

import java.util.UUID;


/**
 * All runtime state lives here, one instance per active sendable. One handle per viewer.
 */
public final class SendableHandle<S extends Sendable> {
    public final S definition;
    public final Receiver receiver;
    public final SendableConfig config;
    public final UUID uuid = UUID.randomUUID();

    private int tick = -1; // Start at -1 so that the first tick (tick 0) happens immediately on add.
    private boolean reversing = false;
    private boolean needsRender = true;
    private boolean expired = false;

    public SendableHandle(S definition, Receiver receiver) {
        this.definition = definition;
        this.config = definition.config();
        this.receiver = receiver;
    }

    public void tick() {
        if (reversing) {
            tick--;
        } else {
            tick++;
        }

        int maxTicks = config.maxTicks();

        if (maxTicks > 0 && (tick >= maxTicks || tick < 0)) {
            if (config.reverseOnExpire() && !reversing) {
                reversing = true;
                tick = maxTicks;
            } else {
                markForExpire();
                return;
            }
        }

        int tickRate = config.tickRate();
        if (tickRate > 0 && tick % tickRate == 0) {
            markForRender();
        }

        if (tick == 0) definition.onAdd(state());
        definition.onTick(state());
    }

    public Percentage progress() {
        return Percentage.ofFraction(tick, definition.config().maxTicks());
    }

    public boolean needsRender() {
        return needsRender;
    }

    public boolean isExpired() {
        return expired;
    }


    public void markForRender() {
        needsRender = true;
    }

    public void markRendered() {
        needsRender = false;
    }

    public void markForExpire() {
        expired = true;
        definition.onExpire(state());
    }


    private SendableState state() {
        return new SendableState(
          receiver,
          tick,
          progress(),
          reversing
        );
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SendableHandle<?> other)) return false;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}