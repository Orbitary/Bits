/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log;

import xyz.bitsquidd.bits.log.pretty.ANSI;
import xyz.bitsquidd.bits.log.pretty.PrettyLogLevel;

import java.lang.reflect.InvocationTargetException;


/**
 * A standard implementation of {@link Logger} that outputs to the system console.
 * <p>
 * This logger uses {@link LogType} to apply visual formatting to messages
 * before printing them to {@link java.lang.System#out}.
 *
 * @since 0.0.10
 */
public class BasicLogger extends Logger {
    private final org.slf4j.Logger slf4j;

    /**
     * @param slf4j the underlying SLF4J logger to wrap
     * @param flags the logging configuration flags to apply
     *
     * @since 0.0.10
     */
    public BasicLogger(org.slf4j.Logger slf4j, LogFlags flags) {
        super(flags);
        this.slf4j = slf4j;
    }

    @Override
    public org.slf4j.Logger slf4j() {
        return slf4j;
    }

    /**
     * Writes a formatted line, discarding its ANSI styling where the console cannot render it.
     *
     * @since 0.0.23
     */
    protected void write(final String line) {
        writeLine(ANSI.render(line));
    }

    /**
     * Writes a single, fully formatted line to the platform's console.
     * <p>
     * Override this to redirect output away from {@link java.lang.System#out}, which
     * some platforms (notably Paper and Minecraft itself) intercept and re-prefix.
     *
     * @param line the formatted line to write
     *
     * @since 0.0.23
     */
    protected void writeLine(final String line) {
        System.out.println(line);
    }

    @Override
    public void debugInternal(final String msg) {
        write(LogType.DEBUG.format(msg));
        onLog(new LogData(msg, LogType.DEBUG));
    }

    @Override
    public void successInternal(final String msg) {
        write(LogType.SUCCESS.format(msg));
        onLog(new LogData(msg, LogType.SUCCESS));
    }

    @Override
    public void infoInternal(final String msg) {
        write(LogType.INFO.format(msg));
        onLog(new LogData(msg, LogType.INFO));
    }

    @Override
    public void warningInternal(final String msg) {
        write(LogType.WARNING.format(msg));
        onLog(new LogData(msg, LogType.WARNING));
    }

    @Override
    public void errorInternal(final String msg) {
        write(LogType.ERROR.format(msg));
        if (flags.logExtendedError()) {
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                write(LogType.ERROR.format("\tat " + element));
            }
        }
        onLog(new LogData(msg, LogType.ERROR));
    }

    /**
     * Logs an exception, unwrapping {@link InvocationTargetException} to reveal
     * the underlying cause if necessary.
     *
     * @since 0.0.13
     */
    @Override
    public void exceptionInternal(final String msg, final Throwable throwable) {
        if (throwable instanceof InvocationTargetException invocationTargetException) {
            Throwable cause = invocationTargetException.getCause();
            if (cause == null) cause = throwable; // Fallback to the original exception if cause is null
            exceptionInternal(msg, cause);
            return;
        }

        Throwable real = throwable;
        while (real.getCause() != null && real.getCause() != real) {
            real = real.getCause();
        }

        StackTraceElement[] stackTrace = real.getStackTrace();
        String header;
        if (stackTrace.length == 0) {
            header = String.format("%s |-> Exception (no stack trace) - %s", msg, real.getMessage());
        } else {
            StackTraceElement origin = stackTrace[0];
            header = String.format(
              "%s |-> Exception at %s.%s (%s:%d) - %s",
              msg,
              origin.getClassName(),
              origin.getMethodName(),
              origin.getFileName(),
              origin.getLineNumber(),
              real.getMessage()
            );
        }
        write(LogType.ERROR.format(header));

        if (flags.logExtendedError()) {
            for (StackTraceElement element : real.getStackTrace()) {
                write(PrettyLogLevel.RED.formatMessage("\tat " + element));
            }
        }
        onLog(new LogData(msg, LogType.FATAL));
    }

}
