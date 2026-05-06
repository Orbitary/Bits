/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.core;

import xyz.bitsquidd.bits.log.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;


public final class LockHelper {
    private LockHelper() {}

    @FunctionalInterface
    public interface CheckedSupplier<T> {
        T get() throws Exception;

    }

    @FunctionalInterface
    public interface CheckedRunnable {
        void run() throws Exception;

    }

    public static void read(ReadWriteLock lock, CheckedRunnable runnable) {
        lock.readLock().lock();
        try {
            runnable.run();
        } catch (Exception e) {
            Logger.exception("Caught exception in read lock.", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static <T> T read(ReadWriteLock lock, CheckedSupplier<T> supplier) {
        lock.readLock().lock();
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException("Exception in read lock.", e);
        } finally {
            lock.readLock().unlock();
        }
    }


    public static void write(ReadWriteLock lock, CheckedRunnable runnable) {
        lock.writeLock().lock();
        try {
            runnable.run();
        } catch (Exception e) {
            Logger.exception("Caught exception in write lock", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static <T> T write(ReadWriteLock lock, CheckedSupplier<T> supplier) {
        lock.writeLock().lock();
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException("Exception in write lock.", e);
        } finally {
            lock.writeLock().unlock();
        }
    }


    public static void withLock(Lock lock, Runnable runnable) {
        lock.lock();
        try {
            runnable.run();
        } catch (Exception e) {
            Logger.exception("Caught exception in lock.", e);
        } finally {
            lock.unlock();
        }
    }

    public static <T> T withLock(Lock lock, CheckedSupplier<T> supplier) {
        lock.lock();
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException("Exception in lock.", e);
        } finally {
            lock.unlock();
        }
    }

}