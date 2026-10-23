/*
 * This file is part of the BiomeBattle project.
 *
 * Copyright (c) 2023-2026 BitSquidd - All Rights Reserved
 *
 * Unauthorized copying, distribution, or disclosure of this file, via any medium, is strictly prohibited.
 * Proprietary and confidential - for internal use only.
 */

package xyz.orbitary.bits.paper.util.runnable;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


public final class Futures {
    private static final Executor MAIN_THREAD_EXECUTOR = runnable -> Runnables.buildBasic(runnable).forced().run();
    private static final Executor ASYNC_THREAD_EXECUTOR = runnable -> Runnables.buildBasic(runnable).forced().async().run();

    private Futures() {}

    public static Executor mainExecutor() {
        return MAIN_THREAD_EXECUTOR;
    }

    public static Executor asyncExecutor() {
        return ASYNC_THREAD_EXECUTOR;
    }

    public static CompletableFuture<@Nullable Void> main(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, MAIN_THREAD_EXECUTOR)
          .orTimeout(15, TimeUnit.SECONDS);
    }

    public static <T extends @Nullable Object> CompletableFuture<T> main(Supplier<T> runnable) {
        return CompletableFuture.supplyAsync(runnable, MAIN_THREAD_EXECUTOR)
          .orTimeout(15, TimeUnit.SECONDS);
    }

    public static CompletableFuture<@Nullable Void> async(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, ASYNC_THREAD_EXECUTOR)
          .orTimeout(15, TimeUnit.SECONDS);
    }

    public static <T extends @Nullable Object> CompletableFuture<T> async(Supplier<T> runnable) {
        return CompletableFuture.supplyAsync(runnable, ASYNC_THREAD_EXECUTOR)
          .orTimeout(15, TimeUnit.SECONDS);
    }

    public static CompletableFuture<@Nullable Void> delayFuture(int ticks) {
        CompletableFuture<@Nullable Void> future = new CompletableFuture<>();
        Runnables.later(() -> future.complete(null), ticks);
        return future;
    }

}
