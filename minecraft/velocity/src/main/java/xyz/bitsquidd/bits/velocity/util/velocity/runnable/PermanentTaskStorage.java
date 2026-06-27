/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.velocity.util.velocity.runnable;

import com.velocitypowered.api.scheduler.ScheduledTask;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A special storage for {@link com.velocitypowered.api.scheduler.ScheduledTask} that should never be cancelled.
 */
public final class PermanentTaskStorage {
    private PermanentTaskStorage() {}

    private static final Set<ScheduledTask> tasks = ConcurrentHashMap.newKeySet();

    public static void add(ScheduledTask task) {
        tasks.add(task);
    }

    public static void remove(ScheduledTask task) {
        tasks.remove(task);
    }

    public static boolean contains(ScheduledTask task) {
        return tasks.contains(task);
    }

}
