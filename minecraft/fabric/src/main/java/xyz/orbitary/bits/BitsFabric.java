/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits;

import xyz.orbitary.bits.util.Safety;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public abstract class BitsFabric extends BitsMinecraft {
    private final PriorityQueue<ScheduledTask> pendingTasks = new PriorityQueue<>(
        Comparator.comparingLong(ScheduledTask::dueNanos).thenComparingLong(ScheduledTask::sequence));
    private final AtomicLong sequence = new AtomicLong();
    private final ExecutorService asyncTasks = Executors.newVirtualThreadPerTaskExecutor();


    protected BitsFabric() {}

    public static BitsFabric get() {
        return (BitsFabric)Bits.get();
    }


    @Override
    public void runLater(Runnable runnable, long delayMs) {
        Objects.requireNonNull(runnable, "runnable");
        var task = new ScheduledTask(runnable,
            System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(Math.max(0, delayMs)), sequence.getAndIncrement());
        synchronized (pendingTasks) {
            if (asyncTasks.isShutdown()) throw new RejectedExecutionException("Bits scheduler has shut down");
            pendingTasks.add(task);
        }
    }

    @Override
    public void runLaterAsync(Runnable runnable, long delayMs) {
        Objects.requireNonNull(runnable, "runnable");
        asyncTasks.submit(() -> {
            try {
                Thread.sleep(Math.max(0, delayMs));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            Safety.safeExecute("Fabric async scheduled task", runnable);
        });
    }

    protected final void tickAll() {
        var ready = new ArrayList<ScheduledTask>();
        var now = System.nanoTime();
        synchronized (pendingTasks) {
            while (!pendingTasks.isEmpty() && now - pendingTasks.peek().dueNanos() >= 0) {
                ready.add(pendingTasks.poll());
            }
        }

        ready.forEach(task -> Safety.safeExecute("Fabric scheduled task", task.runnable()));
    }

    @Override
    public void shutdown() {
        synchronized (pendingTasks) {
            asyncTasks.shutdownNow();
            pendingTasks.clear();
        }
        super.shutdown();
    }


    private record ScheduledTask(Runnable runnable, long dueNanos, long sequence) {}

}
