/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log.pretty;

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
        String timestamp = "";

        StringBuilder prefixBuilder = new StringBuilder(prefix);

        prefixBuilder.insert(0, (emoji + "  "));
        String formattedPrefix = prefixBuilder.toString();

        return String.format(
          "%s %s %s",
          timestamp,
          prefixFormatting.format(formattedPrefix),
          messageFormatting.format(message)
        );
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

