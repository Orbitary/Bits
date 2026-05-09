/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.velocity.util.velocity.runnable;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import com.velocitypowered.api.scheduler.TaskStatus;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.BitsVelocity;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;


public final class Tasks {
    private static final Object plugin = BitsVelocity.get().getPlugin();
    private static final Scheduler scheduler = BitsVelocity.get().getServer().getScheduler();
    private static final EventManager eventManager = BitsVelocity.get().getServer().getEventManager();

    public static Scheduler.TaskBuilder builder(Runnable runnable) {
        return scheduler.buildTask(BitsVelocity.get().getPlugin(), runnable);
    }

    public static Scheduler.TaskBuilder builder(Consumer<ScheduledTask> consumer) {
        return scheduler.buildTask(BitsVelocity.get().getPlugin(), consumer);
    }


    public static @Nullable ScheduledTask cleanup(final @Nullable ScheduledTask task) {
        if (task != null && task.status() == TaskStatus.SCHEDULED) task.cancel();
        return null;
    }

    public static void cleanup(final @Nullable Collection<? extends ScheduledTask> tasks) {
        if (tasks != null) {
            Set.copyOf(tasks).forEach(Tasks::cleanup);
            try {
                tasks.clear();
            } catch (UnsupportedOperationException ignored) {
                // Clearing unmodifiable collections is not supported.
            }
        }
    }

    public static void cleanupAll() {
        scheduler.tasksByPlugin(plugin).forEach(Tasks::cleanup);
    }


    public static void callEvent(Object object) {
        eventManager.fire(object);
    }

}
