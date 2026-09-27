/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log.pretty;

import xyz.bitsquidd.bits.log.LogType;
import xyz.bitsquidd.bits.log.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Defines the visual configuration for a specific log level, including its icon and color scheme.
 * <p>
 * This class combines an emoji, a prefix string, and formatting for both the
 * prefix and the message content to create a unified visual style for console logs.
 *
 * @since 0.0.10
 */
public final class PrettyLogLevel {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String emoji;
    private final String prefix;
    private final FormattingComponents prefixFormatting;
    private final FormattingComponents messageFormatting;

    public PrettyLogLevel(String emoji, String prefix, FormattingComponents prefixFormatting, FormattingComponents messageFormatting) {
        this.emoji = emoji;
        this.prefix = prefix;
        this.prefixFormatting = prefixFormatting;
        this.messageFormatting = messageFormatting;
    }

    public String formatMessage(final String message) {
        String timestamp = LocalTime.now().format(TIME_FORMATTER);
        String callerClass = findCallerClass();

        StringBuilder prefixBuilder = new StringBuilder(prefix);

        prefixBuilder.insert(0, (emoji + "  "));
        String formattedPrefix = prefixBuilder.toString();

        return String.format(
          "[%s] [%s] %s %s",
          timestamp,
          callerClass,
          prefixFormatting.format(formattedPrefix),
          messageFormatting.format(message)
        );
    }

    private static String findCallerClass() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames -> frames
          .map(StackWalker.StackFrame::getDeclaringClass)
          .filter(clazz -> clazz != PrettyLogLevel.class && clazz != LogType.class && !Logger.class.isAssignableFrom(clazz))
          .findFirst()
          .map(Class::getSimpleName)
          .orElse("?"));
    }


    //region Example implementations
    public static final PrettyLogLevel RED = new PrettyLogLevel(
      "", "",
      FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BRIGHT_RED, List.of(ANSI.Style.BOLD)),
      FormattingComponents.of(ANSI.Foreground.BRIGHT_RED, null, List.of())
    );

    public static final PrettyLogLevel YELLOW = new PrettyLogLevel(
      "", "",
      FormattingComponents.of(ANSI.Foreground.BLACK, ANSI.Background.YELLOW, List.of(ANSI.Style.BOLD)),
      FormattingComponents.of(ANSI.Foreground.YELLOW, null, List.of())
    );

    public static final PrettyLogLevel GREEN = new PrettyLogLevel(
      "", "",
      FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.GREEN, List.of(ANSI.Style.BOLD)),
      FormattingComponents.of(ANSI.Foreground.GREEN, null, List.of())
    );

    public static final PrettyLogLevel CYAN = new PrettyLogLevel(
      "", "",
      FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.CYAN, List.of(ANSI.Style.BOLD)),
      FormattingComponents.of(ANSI.Foreground.CYAN, null, List.of())
    );

    public static final PrettyLogLevel BLUE = new PrettyLogLevel(
      "", "",
      FormattingComponents.of(ANSI.Foreground.WHITE, ANSI.Background.BLUE, List.of(ANSI.Style.BOLD)),
      FormattingComponents.of(ANSI.Foreground.BLUE, null, List.of())
    );
    //endregion


}

