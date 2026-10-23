/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.util.runnable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A special storage for runnables that should never be cancelled.
 * Tracks tasks exempt from routine cleanup cancellation.
 */
public final class PermanentRunnableStorage {
    private PermanentRunnableStorage() {}

    private static final Set<Integer> runnables = ConcurrentHashMap.newKeySet();

    public static void add(int taskId) {
        runnables.add(taskId);
    }

    public static void remove(int taskId) {
        runnables.remove(taskId);
    }

    public static boolean contains(int taskId) {
        return runnables.contains(taskId);
    }

}
