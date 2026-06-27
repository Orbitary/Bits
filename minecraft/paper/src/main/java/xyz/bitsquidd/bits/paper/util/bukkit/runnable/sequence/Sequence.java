/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util.bukkit.runnable.sequence;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;
import xyz.bitsquidd.bits.wrapper.Callback;

import java.util.ArrayList;
import java.util.List;


/**
 * A sequence of runnables to be executed at specified ticks.
 */
public final class Sequence {
    private record Step(
      long tick,
      Runnable runnable
    ) {}

    private final List<Step> steps;

    private @Nullable Callback callback;
    private final List<BukkitTask> tasks = new ArrayList<>();

    private Sequence(Builder builder) {
        this.steps = builder.steps;
    }

    public Callback run() {
        cancel();

        callback = Callback.create();
        int longest = steps.stream().mapToInt(step -> (int)step.tick).max().orElse(0);

        steps.forEach(step -> tasks.add((Runnables.later(step.runnable, step.tick))));
        tasks.add(Runnables.later(callback::complete, longest));

        return callback;
    }

    public void cancel() {
        Runnables.cleanup(tasks);
        tasks.clear();

        if (callback != null) callback.complete();
        callback = null;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Buildable<Sequence> {
        private final List<Step> steps = new ArrayList<>();

        public Builder at(long tick, Runnable runnable) {
            this.steps.add(new Step(tick, runnable));
            return this;
        }

        @Override
        public Sequence build() {
            return new Sequence(this);
        }

    }

}
