/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;

import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableConfig;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.wrapper.Percentage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * All runtime state lives here, one instance per active sendable. One handle per receiver.
 */
public final class SendableHandle<S extends Sendable> {
    private final S definition;
    private final Receiver receiver;
    private final SendableManager<? super S> manager; // Internal use only for manager callbacks.
    private final SendableConfig config;
    private final UUID uuid = UUID.randomUUID();

    private final Map<Key, Object> data = new ConcurrentHashMap<>();

    private volatile long tick = -1; // Start at -1 so that the first tick (tick 0) happens immediately on add.
    private volatile boolean hasAdded = false;
    private volatile boolean reversing = false;
    private volatile boolean needsRender = true;
    private volatile boolean expired = false;

    public SendableHandle(S definition, Receiver receiver, SendableManager<? super S> manager) {
        this.definition = definition;
        this.receiver = receiver;
        this.config = definition.config();
        this.manager = manager;
    }

    public SendableHandle<S> copyFor(Receiver newReceiver) {
        SendableHandle<S> handle = new SendableHandle<>(definition, newReceiver, manager);
        handle.data.putAll(data);
        handle.tick = this.tick;
        handle.hasAdded = false;
        handle.reversing = this.reversing;
        handle.needsRender = this.needsRender;
        handle.expired = this.expired;
        return handle;
    }

    @ApiStatus.Internal
    public void bits$tick() {
        if (reversing) {
            tick--;
        } else {
            tick++;
        }

        long maxTicks = config.maxTicks();

        if (tick >= maxTicks || tick < 0) {
            if (config.reverseOnExpire() && !reversing) {
                reversing = true;
                tick = maxTicks;
            } else {
                bits$markForExpire();
                return;
            }
        }

        if (definition.needsRender(state(receiver))) bits$markForRender();

        if (!hasAdded) triggerAdd();
        triggerTick();
    }


    public SendableHandle<S> data(Key key, Object value) {
        data.put(key, value);
        return this;
    }

    public @Nullable Object getData(Key key) {
        return data.get(key);
    }


    //region Experimental

    /**
     * <b>Generally unsupported, most cases should never need this. Use with caution.</b>
     * <p>
     * Manually set the tick of this sendable. Use with caution.
     */
    @ApiStatus.Experimental
    public void setTick(long tick) {
        this.tick = tick;
    }

    /**
     * <b>Generally unsupported, most cases should never need this. Use with caution.</b>
     * <p>
     * Manually set the reversing state of this sendable.
     */
    @ApiStatus.Experimental
    public void setReversing(boolean reversing) {
        this.reversing = reversing;
    }

    //endregion

    //region Flag marking
    public void bits$markForRender() {
        needsRender = true;
    }

    @ApiStatus.Internal
    public void bits$markRendered() {
        needsRender = false;
    }

    @ApiStatus.Internal
    public void bits$markForExpire() {
        expired = true;
        triggerExpire();
    }
    //endregion


    private void triggerAdd() {
        hasAdded = true;
        definition.onAdd(state(receiver));
        manager.onAdd(receiver, this);
    }

    private void triggerTick() {
        definition.onTick(state(receiver));
        manager.onTick(receiver, this);
    }

    private void triggerExpire() {
        definition.onExpire(state(receiver));
        manager.onExpire(receiver, this);
    }


    //region Getters
    public UUID uuid() {
        return uuid;
    }

    public S definition() {
        return definition;
    }

    public SendableConfig config() {
        return config;
    }

    public long getTick() {
        return tick;
    }

    public Percentage getProgress() {
        return Percentage.ofFraction(tick, definition.config().maxTicks());
    }

    public boolean needsRender() {
        return needsRender;
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean isReversing() {
        return reversing;
    }


    public SendableState state(Receiver receiver) {
        return new SendableState(
          receiver,
          tick,
          getProgress(),
          reversing,
          this
        );
    }
    //endregion


    //region Java Object Overrides
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
    //endregion

}