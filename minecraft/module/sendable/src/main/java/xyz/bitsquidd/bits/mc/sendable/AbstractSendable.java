/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;

import xyz.bitsquidd.bits.util.core.AnnotationHelper;


/**
 * An abstract class representing a sendable entity that can be updated and ticked at a specified rate.
 * <p>
 * Developer Note: Each subclass must implement their own component getting logic.
 */
public abstract class AbstractSendable {
    protected int tickRate; // TODO: lateinit
    protected int maxTicks; // lateinit
    protected boolean reverseOnExpire; // lateinit
    protected boolean persistent; // lateinit
    protected int priority; // lateinit


    protected int tick = -1; // Start at -1 so that the first tick is 0 after the first tick() call.
    protected boolean isReversing = false;

    private boolean needsUpdate = true;
    private boolean needsExpire = false;

    protected AbstractSendable() {
        this.tickRate = AnnotationHelper.getValueOrDefault(this.getClass(), Sendable.class, "tickRate");
        this.maxTicks = AnnotationHelper.getValueOrDefault(this.getClass(), Sendable.class, "maxTicks");
        this.reverseOnExpire = AnnotationHelper.getValueOrDefault(this.getClass(), Sendable.class, "reverseOnExpire");
        this.persistent = AnnotationHelper.getValueOrDefault(this.getClass(), Sendable.class, "persistent");
        this.priority = AnnotationHelper.getValueOrDefault(this.getClass(), Sendable.class, "priority");
    }

    public final int tickRate() {
        return tickRate;
    }

    public final int maxTicks() {
        return maxTicks;
    }

    public final boolean reverseOnExpire() {
        return reverseOnExpire;
    }

    public final boolean isPersistent() {
        return persistent;
    }

    public final int priority() {
        return priority;
    }


    // We run tick after getting all required updates etc. so we can safely reset update flags.
    // Tick is called once per sendable instance, not per player.
    public final void tick() {
        if (isReversing) {
            tick--;
        } else {
            tick++;
        }

        if (maxTicks > 0 && (tick >= maxTicks || tick < 0)) {
            if (reverseOnExpire && !isReversing) {
                isReversing = true;
                tick = maxTicks;
            } else {
                expire(); // Remove the sendable from all players.
            }
        }

        if (tickRate > 0 && tick % tickRate == 0) {
            needsUpdate = true;
            onTick();
        }
    }

    protected void onTick() {}

    public void onAdd(BiomePlayer player) {}

    public void onRemove(BiomePlayer player) {}


    //region Reversing & Looping
    private void expire() {
        this.needsExpire = true;
    }

    protected final void setReversing(boolean reversing) {
        this.isReversing = reversing;
    }
    //endregion


    //region Updates
    public final void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public boolean needsUpdate() {
        return needsUpdate;
    }

    public boolean needsExpire() {
        return needsExpire;
    }
    //endregion


}
