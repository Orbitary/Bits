/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jetbrains.annotations.Nullable;

import java.io.File;


public final class Log4jConsole {
    private static final String ANSI_PATTERN = "\\x1B\\[[0-9;]*m";

    private Log4jConsole() {}

    public static org.apache.logging.log4j.Logger create(String name, File logFile) {
        LoggerContext context = (LoggerContext)LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();

        if (configuration.getLoggers().containsKey(name)) return LogManager.getLogger(name);

        LoggerConfig loggerConfig = new LoggerConfig(name, Level.ALL, false);
        loggerConfig.addAppender(consoleAppender(name), null, null);
        loggerConfig.addAppender(fileAppender(name, logFile), null, null);

        configuration.addLogger(name, loggerConfig);
        context.updateLoggers();

        return LogManager.getLogger(name);
    }

    /**
     * Creates a logger that writes only to the host server's own log file ({@code latest.log}), so output that
     * bypasses the host's console pipeline is still recorded there. The console is left alone to avoid duplicates.
     *
     * @return the logger, or null if the host has no appender named {@code File}
     *
     * @since 0.1.8
     */
    public static org.apache.logging.log4j.@Nullable Logger hostFile(String name) {
        LoggerContext context = (LoggerContext)LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();

        Appender host = configuration.getAppender("File");
        if (host == null) return null;

        String loggerName = name + "HostFile";
        if (!configuration.getLoggers().containsKey(loggerName)) {
            LoggerConfig loggerConfig = new LoggerConfig(loggerName, Level.ALL, false);
            loggerConfig.addAppender(host, null, null);
            configuration.addLogger(loggerName, loggerConfig);
            context.updateLoggers();
        }
        return LogManager.getLogger(loggerName);
    }

    @SuppressWarnings("deprecation")
    private static Appender consoleAppender(String name) {
        Appender appender = ConsoleAppender.newBuilder()
          .setName(name + "Console")
          .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
          .setDirect(true)
          .setLayout(PatternLayout.newBuilder().withPattern("%msg%n").build())
          .build();
        appender.start();
        return appender;
    }

    @SuppressWarnings("deprecation")
    private static Appender fileAppender(String name, File logFile) {
        Appender appender = FileAppender.newBuilder()
          .setName(name + "File")
          .withFileName(logFile.getPath())
          .withAppend(true)
          .withCreateOnDemand(true)
          .setLayout(PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss}] %replace{%msg}{" + ANSI_PATTERN + "}{}%n").build())
          .build();
        appender.start();
        return appender;
    }

}
