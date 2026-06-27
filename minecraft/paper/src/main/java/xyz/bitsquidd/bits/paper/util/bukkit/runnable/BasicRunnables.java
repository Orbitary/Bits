/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util.bukkit.runnable;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public final class BasicRunnables extends Runnables {
    private final Runnable task;

    BasicRunnables(Builder builder) {
        super(builder);
        this.task = builder.task;
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
        if (isAsync) {
            return runnable.runTaskAsynchronously(plugin);
        } else {
            return runnable.runTask(plugin);
        }
    }

    public static final class Builder extends AbstractRunnableBuilder<BasicRunnables, Builder> {
        private final Runnable task;

        public Builder(Runnable task) {
            this.task = task;
        }

        @Override
        public BasicRunnables buildInternal() {
            return new BasicRunnables(this);
        }

    }


}
