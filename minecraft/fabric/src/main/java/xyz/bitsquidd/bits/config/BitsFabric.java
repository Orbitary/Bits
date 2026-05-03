/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config;

import xyz.bitsquidd.bits.log.FabricBitsLogger;
import xyz.bitsquidd.bits.log.Logger;

import java.util.ArrayList;
import java.util.List;


public abstract class BitsFabric extends BitsMinecraft {
    private final org.slf4j.Logger slf4j;

    private final List<ScheduledTask> pendingTasks = new ArrayList<>();

    public BitsFabric(org.slf4j.Logger slf4j) {
        this.slf4j = slf4j;
    }


    public static BitsFabric get() {
        return (BitsFabric)Bits.get();
    }


    @Override
    protected Logger createLogger() {
        return new FabricBitsLogger(slf4j, Logger.LogFlags.defaultFlags());
    }

    @Override
    public void runLater(Runnable runnable, long delayMs) {
        pendingTasks.add(new ScheduledTask(runnable, delayMs));
    }

    @Override
    public void runLaterAsync(Runnable runnable, long delayMs) {
        Thread.ofVirtual().start(runnable);
    }

    protected final void tickAll() {
        pendingTasks.removeIf(ScheduledTask::tick);
    }


    public abstract void registerAllCommands();


    private static final class ScheduledTask {
        private final Runnable runnable;
        private long ticksRemaining;

        ScheduledTask(Runnable runnable, long delayTicks) {
            this.runnable = runnable;
            this.ticksRemaining = delayTicks;
        }

        boolean tick() {
            if (--ticksRemaining <= 0) {
                runnable.run();
                return true;
            }
            return false;
        }

    }

}
