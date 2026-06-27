/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log;

import xyz.bitsquidd.bits.log.pretty.ANSI;
import xyz.bitsquidd.bits.log.pretty.FormattingComponents;
import xyz.bitsquidd.bits.log.pretty.PrettyLogLevel;

import java.util.Arrays;
import java.util.List;


/**
 * Represents a specific log category with its own unique identifier, priority, and visual style.
 *
 * @param id       the unique string identifier for this log type
 * @param priority the execution priority associated with this log level
 * @param logLevel the visual formatting component defining how this type is rendered in console
 *
 * @since 0.0.10
 */
public record LogType(
  String id,
  int priority,
  PrettyLogLevel logLevel
) {
    public String format(String input) {
        return logLevel.formatMessage(input);
    }


    public static final LogType DEBUG = new LogType(
      "debug",
      1,
      new PrettyLogLevel(
        "🔎", "DEBUG",
        FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BLUE, List.of(ANSI.Style.BOLD)),
        FormattingComponents.of(ANSI.Foreground.BLUE, null, List.of())
      )
    );

    public static final LogType SUCCESS = new LogType(
      "success",
      10,
      new PrettyLogLevel(
        "✅", "SUCCESS",
        FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BRIGHT_GREEN, List.of(ANSI.Style.BOLD)),
        FormattingComponents.of(ANSI.Foreground.BRIGHT_GREEN, null, List.of())
      )
    );

    public static final LogType INFO = new LogType(
      "info",
      20,
      new PrettyLogLevel(
        "ℹ️", "INFO",
        FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BRIGHT_BLUE, List.of(ANSI.Style.BOLD)),
        FormattingComponents.of(ANSI.Foreground.BRIGHT_BLUE, null, List.of())
      )
    );

    public static final LogType WARNING = new LogType(
      "warning",
      50,
      new PrettyLogLevel(
        "⚠️", "WARN",
        FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BRIGHT_YELLOW, List.of(ANSI.Style.BOLD)),
        FormattingComponents.of(ANSI.Foreground.BRIGHT_YELLOW, null, List.of())
      )
    );

    public static final LogType ERROR = new LogType(
      "error",
      60,
      new PrettyLogLevel(
        "🚨", "ERROR",
        FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BRIGHT_RED, List.of(ANSI.Style.BOLD)),
        FormattingComponents.of(ANSI.Foreground.BRIGHT_RED, null, List.of())
      )
    );

    public static final LogType FATAL = new LogType(
      "fatal",
      100,
      new PrettyLogLevel(
        "☠️", "FATAL",
        FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BRIGHT_RED, List.of(ANSI.Style.BOLD)),
        FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BRIGHT_RED, List.of(ANSI.Style.BOLD))
      )
    );


    public enum Standard {
        DEBUG(LogType.DEBUG),
        SUCCESS(LogType.SUCCESS),
        INFO(LogType.INFO),
        WARNING(LogType.WARNING),
        ERROR(LogType.ERROR),
        FATAL(LogType.FATAL);

        public final LogType logType;

        Standard(LogType logType) {
            this.logType = logType;
        }

        public static Standard fromType(LogType type) {
            return Arrays.stream(values()).filter(standard -> standard.logType.id().equals(type.id())).findFirst().orElse(INFO);
        }
    }

}
