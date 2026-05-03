/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util.bukkit.runnable;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.bitsquidd.bits.config.BitsMinecraft;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public final class TimerRunnables extends Runnables {
    private final Consumer<Integer> onTick;

    private final long delay;
    private final long period;

    private final Function<Integer, Boolean> stopCondition;
    private final Consumer<Integer> onStop;


    TimerRunnables(Builder builder) {
        super(builder);
        this.onTick = builder.onTick;

        this.period = builder.period;
        this.delay = builder.delay;

        this.stopCondition = builder.stopCondition;
        this.onStop = builder.onStop;
    }

    @Override
    public BukkitRunnable asRunnable() {
        if (isPausable) {
            return new BukkitRunnable() {
                int tick = -(int)delay;

                @Override
                public void run() {
                    tick++;

                    if (tick < 0 || tick % period != 0 || BitsMinecraft.get().isPaused()) return;

                    if (stopCondition.apply(tick)) {
                        onStop.accept(tick);
                        this.cancel();
                        return;
                    } else {
                        onTick.accept(tick);
                    }
                }
            };
        } else {
            return new BukkitRunnable() {
                int tick = 0;

                @Override
                public void run() {
                    if (stopCondition.apply(tick)) {
                        onStop.accept(tick);
                        this.cancel();
                        return;
                    } else {
                        onTick.accept(tick);
                    }

                    tick++;
                }
            };
        }


    }

    @Override
    protected BukkitTask createTask(BukkitRunnable runnable) {
        long effectiveDelay = isPausable ? 0 : delay;
        long effectivePeriod = isPausable ? 1 : period;

        if (isAsync) {
            return runnable.runTaskTimerAsynchronously(plugin, effectiveDelay, effectivePeriod);
        } else {
            return runnable.runTaskTimer(plugin, effectiveDelay, effectivePeriod);
        }
    }

    public static final class Builder extends AbstractRunnableBuilder<TimerRunnables, Builder> {
        private final Consumer<Integer> onTick;

        private final long delay;
        private final long period;

        private Function<Integer, Boolean> stopCondition = tick -> false;
        private Consumer<Integer> onStop = (tick) -> {};

        public Builder(Consumer<Integer> onTick, long delay, long period) {
            this.onTick = onTick;
            this.delay = delay;
            this.period = period;
        }

        public Builder stopCondition(Function<Integer, Boolean> stopCondition) {
            final Function<Integer, Boolean> previous = this.stopCondition;
            this.stopCondition = tick -> previous.apply(tick) || stopCondition.apply(tick);
            return this;
        }

        public Builder stopCondition(Supplier<Boolean> stopCondition) {
            return stopCondition(tick -> stopCondition.get());
        }

        public Builder onStop(Consumer<Integer> onStop) {
            this.onStop = onStop;
            return this;
        }

        public Builder onStop(Runnable onStop) {
            return onStop(tick -> onStop.run());
        }


        @Override
        public TimerRunnables buildInternal() {
            return new TimerRunnables(this);
        }

    }


}
