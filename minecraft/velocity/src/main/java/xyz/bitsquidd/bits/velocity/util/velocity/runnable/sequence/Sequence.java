/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.velocity.util.velocity.runnable.sequence;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.velocity.util.velocity.runnable.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A sequence of runnables to be executed at specified ticks.
 */
public final class Sequence {
    private record Step(
      long ms,
      Runnable runnable
    ) {}

    private final List<Step> steps;

    private Sequence(Builder builder) {
        this.steps = builder.steps;
    }

    public void run() {
        steps.forEach(step -> Tasks.builder(step.runnable).delay(step.ms, TimeUnit.MILLISECONDS).schedule());
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
