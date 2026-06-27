/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util;

import org.bukkit.Bukkit;

import xyz.bitsquidd.bits.wrapper.Percentage;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;


public final class Performance {
    private Performance() {}

    public static final class TPS {
        private TPS() {}

        public static final double MAX_TPS = 20.0;
        public static final double MIN_TICK_TIME = 50.0;

        public static double ms() {
            return Bukkit.getAverageTickTime();
        }

        public static double current() {
            return Math.min(20.0, 1000.0 / ms());
        }

    }


    public static final class Memory {
        private static final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();

        private Memory() {}

        public static long max() {
            MemoryUsage heapUsage = bean.getHeapMemoryUsage();
            return heapUsage.getMax();
        }

        public static long free() {
            MemoryUsage heapUsage = bean.getHeapMemoryUsage();
            long committed = heapUsage.getCommitted();
            long used = heapUsage.getUsed();
            return committed - used;
        }

        public static long used() {
            MemoryUsage heapUsage = bean.getHeapMemoryUsage();
            return heapUsage.getUsed();
        }

        public static Percentage percentageUsed() {
            return Percentage.ofFraction(used(), max());
        }

    }

}
