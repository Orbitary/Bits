/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util.bukkit.runnable;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public final class LaterRunnables extends Runnables {
    private final Runnable task;

    private final long delay;


    LaterRunnables(Builder builder) {
        super(builder);
        this.task = builder.task;
        this.delay = builder.delay;
    }

    @Override
    public BukkitRunnable asRunnable() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        };
    }

    @Override
    protected BukkitTask createTask(BukkitRunnable runnable) {
        if (isPausable) {
            // Pausable "laters" must be delegated to a timer for correct functionality.
            return Runnables.buildTimer(task, delay, 1, 1)
              .async(isAsync)
              .forced(isForced)
              .pausable()
              .run();
        }

        if (isAsync) {
            return runnable.runTaskLaterAsynchronously(plugin, delay);
        } else {
            return runnable.runTaskLater(plugin, delay);
        }
    }

    public static final class Builder extends AbstractRunnableBuilder<LaterRunnables, Builder> {
        private final Runnable task;
        private final long delay;

        public Builder(Runnable task, long delay) {
            this.task = task;
            this.delay = delay;
        }

        @Override
        public LaterRunnables buildInternal() {
            return new LaterRunnables(this);
        }

    }


}
