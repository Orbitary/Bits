/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.util.core;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static org.junit.jupiter.api.Assertions.*;

class LockHelperTest {
    private enum Mode { READ, WRITE, EXCLUSIVE }

    private static int execute(Mode mode, ReentrantReadWriteLock lock, LockHelper.CheckedSupplier<Integer> action) {
        return switch (mode) {
            case READ -> LockHelper.read(lock, action);
            case WRITE -> LockHelper.write(lock, action);
            case EXCLUSIVE -> LockHelper.withLock(lock.writeLock(), action);
        };
    }

    private static void assertReleased(ReentrantReadWriteLock lock) {
        assertEquals(0, lock.getReadLockCount());
        assertFalse(lock.isWriteLocked());
    }

    @Test
    void suppliersRunUnderTheirLockAndReturnTheirValue() {
        for (var mode : Mode.values()) {
            var lock = new ReentrantReadWriteLock();
            assertEquals(42, execute(mode, lock, () -> {
                assertTrue(lock.getReadHoldCount() == 1 || lock.isWriteLockedByCurrentThread());
                return 42;
            }));
            assertReleased(lock);
        }
    }

    @Test
    void checkedFailuresRetainTheirCauseAndReleaseTheLock() {
        for (var mode : Mode.values()) {
            var lock = new ReentrantReadWriteLock();
            var cause = new IOException("failure");
            var error = assertThrows(RuntimeException.class, () -> execute(mode, lock, () -> { throw cause; }));
            assertSame(cause, error.getCause());
            assertReleased(lock);
        }
    }

    @Test
    void errorsPropagateUnchangedAndStillReleaseTheLock() {
        for (var mode : Mode.values()) {
            var lock = new ReentrantReadWriteLock();
            var cause = new AssertionError("failure");
            assertSame(cause, assertThrows(AssertionError.class, () -> execute(mode, lock, () -> { throw cause; })));
            assertReleased(lock);
        }
    }
}
