/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util.bukkit.runnable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.config.BitsPaper;
import xyz.bitsquidd.bits.lifecycle.builder.ExtendableBuildable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;


public sealed abstract class Runnables permits BasicRunnables, LaterRunnables, TimerRunnables {
    protected static final JavaPlugin plugin = BitsPaper.get().plugin();
    protected static final BukkitScheduler scheduler = Bukkit.getScheduler();

    protected final boolean isForced;
    protected final boolean isAsync;
    protected final boolean isPausable;

    Runnables(AbstractRunnableBuilder<?, ?> builder) {
        this.isForced = builder.isForced;
        this.isAsync = builder.isAsync;
        this.isPausable = builder.isPausable;
    }


    public final BukkitTask run() {
        BukkitTask task = createTask(asRunnable());
        if (isForced) PermanentRunnableStorage.add(task.getTaskId());
        return task;
    }


    protected abstract BukkitTask createTask(BukkitRunnable runnable);

    public abstract BukkitRunnable asRunnable();


    //region Static Utilities
    //region Tasks
    public static BukkitTask basic(final Runnable runnable) {
        return new BasicRunnables.Builder(runnable).run();
    }

    public static BukkitTask async(final Runnable runnable) {
        return new BasicRunnables.Builder(runnable).async().forced().run();
    }

    public static BasicRunnables.Builder buildBasic(final Runnable runnable) {
        return new BasicRunnables.Builder(runnable);
    }


    public static BukkitTask later(final Runnable runnable, final long delay) {
        return new LaterRunnables.Builder(runnable, delay).run();
    }

    public static LaterRunnables.Builder buildLater(final Runnable runnable, final long delay) {
        return new LaterRunnables.Builder(runnable, delay);
    }


    public static BukkitTask timer(final Consumer<Integer> onTick, final long delay, final long period) {
        return new TimerRunnables.Builder(onTick, delay, period).run();
    }

    public static BukkitTask timer(final Runnable onTick, final long delay, final long period) {
        return new TimerRunnables.Builder(tick -> onTick.run(), delay, period).run();
    }

    public static TimerRunnables.Builder buildTimer(final Consumer<Integer> onTick, final long delay, final long period) {
        return new TimerRunnables.Builder(onTick, delay, period);
    }

    public static TimerRunnables.Builder buildTimer(final Runnable onTick, final long delay, final long period) {
        return new TimerRunnables.Builder(tick -> onTick.run(), delay, period);
    }

    public static TimerRunnables.Builder buildTimer(final Consumer<Integer> onTick, final long delay, final long period, final int amount) {
        return new TimerRunnables.Builder(onTick, delay, period).stopCondition(tick -> tick >= amount);
    }

    public static TimerRunnables.Builder buildTimer(final Runnable onTick, final long delay, final long period, final int amount) {
        return new TimerRunnables.Builder(tick -> onTick.run(), delay, period).stopCondition(tick -> tick >= amount);
    }
    //endregion

    //region Cleanup
    public static void cleanupAll() {
        scheduler.cancelTasks(BitsPaper.get().plugin());
    }

    public static @Nullable BukkitTask cleanup(final @Nullable BukkitTask bukkitTask) {
        if (bukkitTask != null && !bukkitTask.isCancelled()) bukkitTask.cancel();
        return null;
    }

    public static @Nullable Integer cleanup(final @Nullable Integer taskId) {
        if (taskId != null) scheduler.cancelTask(taskId);
        return null;
    }

    public static void cleanup(final @Nullable Collection<? extends BukkitTask> tasks) {
        if (tasks != null) {
            Set.copyOf(tasks).forEach(Runnables::cleanup);
            try {
                tasks.clear();
            } catch (UnsupportedOperationException ignored) {
                // Clearing unmodifiable collections is not supported.
            }
        }
    }
    //endregion

    //region Thread
    public static void callEvent(final Event event) {
        Runnable eventRunnable = event::callEvent;
        if (event.isAsynchronous()) {
            runOnAsyncThread(eventRunnable);
        } else {
            runOnMainThread(eventRunnable);
        }
    }

    public static void ensureThread(final boolean async, final Runnable runnable) {
        if (async) {
            runOnAsyncThread(runnable);
        } else {
            runOnMainThread(runnable);
        }
    }

    public static void runOnMainThread(final Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            buildBasic(runnable)
              .forced()
              .run();
        }
    }

    public static void runOnAsyncThread(final Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            buildBasic(runnable)
              .async()
              .forced()
              .run();
        } else {
            runnable.run();
        }
    }
    //endregion


    //endregion


    public abstract static class AbstractRunnableBuilder<B extends Runnables, SELF extends AbstractRunnableBuilder<B, SELF>> extends ExtendableBuildable<B, SELF> {
        protected boolean isForced = false;
        protected boolean isAsync = false;
        protected boolean isPausable = false;

        public SELF forced() {
            return forced(true);
        }

        public SELF forced(boolean forced) {
            this.isForced = forced;
            return self();
        }

        public SELF async() {
            return async(true);
        }

        public SELF async(boolean async) {
            this.isAsync = async;
            return self();
        }

        public SELF pausable() {
            this.isPausable = true;
            return self();
        }

        protected abstract B buildInternal();

        @Override
        public final B build() {
            throw new UnsupportedOperationException("Runnables must be built using run()");
        }

        public BukkitTask run() {
            return buildInternal().run();
        }

    }

}
