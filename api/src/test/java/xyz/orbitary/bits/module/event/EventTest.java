/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.event;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTest {
    static final class Sample extends Event {}

    @Test
    void newEventIsNotCancelled() {
        final var event = new Sample();
        assertFalse(event.cancelled());
        assertNull(event.cancelReason());
    }

    @Test
    void cancelRecordsTheReason() {
        final var event = new Sample();
        event.cancel("because");
        assertTrue(event.cancelled());
        assertEquals("because", event.cancelReason());
    }

    @Test
    void cancellingAgainKeepsTheFirstReason() {
        final var event = new Sample();
        event.cancel("first");
        event.cancel("second");
        assertEquals("first", event.cancelReason());
    }

    @Test
    void concurrentCancelsAgreeOnOneReason() throws Exception {
        final var event = new Sample();
        final int threads = 8;
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        final var start = new CountDownLatch(1);
        final List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            final String reason = "reason" + t;
            futures.add(pool.submit(() -> {
                start.await();
                event.cancel(reason);
                return event.cancelReason();
            }));
        }
        start.countDown();
        final var seen = new ArrayList<Object>();
        for (final Future<?> future : futures) seen.add(future.get(10, TimeUnit.SECONDS));
        pool.shutdown();

        assertTrue(seen.stream().allMatch(reason -> reason.equals(event.cancelReason())), "every thread must read the winning reason");
    }

}
