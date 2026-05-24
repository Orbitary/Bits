/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log;

import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.Bits;


/**
 * The core logging abstraction for the Bits library.
 * <p>
 * Implementations are responsible for directing log output to the platform's
 * console or log files. The logger must be initialised via {@link Bits}
 * before it can be accessed globally.
 *
 * @since 0.0.10
 */
public abstract class Logger {
    private static @Nullable Logger instance;

    protected final LogFlags flags;

    public record LogData(
      String msg,
      LogType type
    ) {}

    public record LogFlags(
      boolean logSuccess,
      boolean logDebug,
      boolean logInfo,
      boolean logWarn,
      boolean logError,
      boolean logException,

      boolean logExtendedError
    ) {
        public static LogFlags defaultFlags() {
            return new LogFlags(true, false, true, true, true, true, false);
        }

    }


    public Logger(LogFlags flags) {
        this.flags = flags;
        if (instance != null) throw new IllegalStateException("Logger is already initialized!");
        instance = this;
    }

    public static Logger get() {
        if (instance == null) throw new IllegalStateException("Logger is not initialized! Ensure you instantiate a `new Logger()` implementation during your application's startup.");
        return instance;
    }


    public abstract org.slf4j.Logger slf4j();


    protected abstract void debugInternal(final String msg);


    /**
     * Invoked every time a log message is processed.
     * <p>
     * Override this method to implement custom log handling, such as sending
     * notifications to admin users or external monitoring services.
     *
     * @param logData the data associated with the log event
     *
     * @since 0.0.10
     */
    protected void onLog(LogData logData) {}

    public static void debug(final String msg) {
        if (!get().flags.logDebug()) return;
        get().debugInternal(msg);
    }

    protected abstract void successInternal(final String msg);

    public static void success(final String msg) {
        if (!get().flags.logSuccess()) return;
        get().successInternal(msg);
    }

    protected abstract void infoInternal(final String msg);

    public static void info(final String msg) {
        if (!get().flags.logInfo()) return;
        get().infoInternal(msg);
    }

    protected abstract void warningInternal(final String msg);

    public static void warn(final String msg) {
        if (!get().flags.logWarn()) return;
        get().warningInternal(msg);
    }

    protected abstract void errorInternal(final String msg);

    public static void error(final String msg) {
        if (!get().flags.logError()) return;
        get().errorInternal(msg);
    }

    protected abstract void exceptionInternal(final String msg, final Throwable throwable);

    public static void exception(final String msg, final Throwable throwable) {
        if (!get().flags.logException()) return;
        get().exceptionInternal(msg, throwable);
    }

}
