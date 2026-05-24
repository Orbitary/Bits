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
        if (instance != null) throw new IllegalStateException("Logger is already initialized");
        instance = this;
    }

    public static Logger get() {
        if (instance == null) throw new IllegalStateException("Logger is not initialized");
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

    public static void debug(final String format, Object... args) {
        if (!get().flags.logDebug()) return;
        get().debugInternal(formatMessage(format, args));
    }


    protected abstract void successInternal(final String msg);

    public static void success(final String msg) {
        if (!get().flags.logSuccess()) return;
        get().successInternal(msg);
    }

    public static void success(final String format, Object... args) {
        if (!get().flags.logSuccess()) return;
        get().successInternal(formatMessage(format, args));
    }


    protected abstract void infoInternal(final String msg);

    public static void info(final String msg) {
        if (!get().flags.logInfo()) return;
        get().infoInternal(msg);
    }

    public static void info(final String format, Object... args) {
        if (!get().flags.logInfo()) return;
        get().infoInternal(formatMessage(format, args));
    }


    protected abstract void warningInternal(final String msg);

    public static void warn(final String msg) {
        if (!get().flags.logWarn()) return;
        get().warningInternal(msg);
    }

    public static void warn(final String format, Object... args) {
        if (!get().flags.logWarn()) return;
        get().warningInternal(formatMessage(format, args));
    }


    protected abstract void errorInternal(final String msg);

    public static void error(final String msg) {
        if (!get().flags.logError()) return;
        get().errorInternal(msg);
    }

    public static void error(final String format, Object... args) {
        if (!get().flags.logError()) return;
        get().errorInternal(formatMessage(format, args));
    }


    protected abstract void exceptionInternal(final String msg, final Throwable throwable);

    public static void exception(final String msg, final Throwable throwable) {
        if (!get().flags.logException()) return;
        get().exceptionInternal(msg, throwable);
    }

    public static void exception(final String format, final Throwable throwable, Object... args) {
        if (!get().flags.logException()) return;
        get().exceptionInternal(formatMessage(format, args), throwable);
    }

    //region Utils
    private static String formatMessage(String messagePattern, Object[] args) {
        if (args.length == 0) return messagePattern;

        StringBuilder result = new StringBuilder(messagePattern.length() + 50);
        int argIndex = 0;
        int i = 0;

        while (i < messagePattern.length() && argIndex < args.length) {
            int delimiterPos = messagePattern.indexOf("{}", i);

            // No more placeholders
            if (delimiterPos == -1) {
                result.append(messagePattern.substring(i));
                break;
            }

            // Check if the delimiter is escaped
            if (isEscaped(messagePattern, delimiterPos)) {
                result.append(messagePattern, i, delimiterPos - 1);
                result.append("{");
                i = delimiterPos + 1;
            } else {
                result.append(messagePattern, i, delimiterPos);
                result.append(args[argIndex++]);
                i = delimiterPos + 2;
            }
        }

        // Append remaining text
        result.append(messagePattern.substring(i));
        return result.toString();
    }

    private static boolean isEscaped(String message, int position) {
        return position > 0 && message.charAt(position - 1) == '\\';
    }
    //endregion

}
