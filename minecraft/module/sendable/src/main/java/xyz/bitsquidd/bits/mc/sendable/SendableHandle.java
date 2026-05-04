/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.mc.sendable.impl.AbstractSendable;


public final class SendableHandle {
    private final AbstractSendable definition;

    private int tick = 0;
    private boolean reversing = false;
    private boolean needsUpdate = true;
    private boolean expired = false;

    public SendableHandle(AbstractSendable definition) {
        this.definition = definition;
    }

    public void tick() {
        if (reversing) {
            tick--;
        } else {
            tick++;
        }

        SendableConfig config = definition.config();
        int maxTicks = config.maxTicks;

        if (maxTicks > 0 && (tick >= maxTicks || tick < 0)) {
            if (config.reverseOnExpire && !reversing) {
                reversing = true;
                tick = maxTicks;
            } else {
                expired = true;
                return;
            }
        }

        int tickRate = config.tickRate;
        if (tickRate > 0 && tick % tickRate == 0) {
            needsUpdate = true;
        }
    }

    public float progress() {
        int maxTicks = definition.config().maxTicks;
        if (maxTicks <= 0) return 1.0f;
        return (float)tick / maxTicks;
    }

    public boolean needsUpdate() {
        return needsUpdate;
    }

    public boolean isExpired() {
        return expired;
    }

    public void markUpdated() {
        needsUpdate = false;
    }

}