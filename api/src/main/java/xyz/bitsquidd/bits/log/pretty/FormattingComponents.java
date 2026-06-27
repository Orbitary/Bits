/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log.pretty;

import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * A container for ANSI color and style components that can be applied to a string.
 * <p>
 * This class encapsulates a foreground color, background color, and a list of
 * styles (like bold or italic) to provide a consistent way to format console text.
 *
 * @since 0.0.10
 */
public final class FormattingComponents {
    private final @Nullable ANSI.Foreground foreground;
    private final @Nullable ANSI.Background background;
    private final @Nullable List<ANSI.Style> styles;

    private FormattingComponents(@Nullable ANSI.Foreground foreground, @Nullable ANSI.Background background, @Nullable List<ANSI.Style> styles) {
        this.foreground = foreground;
        this.background = background;
        this.styles = styles;
    }

    /**
     * Creates a new formatting container with the specified ANSI components.
     *
     * @param foreground the text color to apply, or null for default
     * @param background the background color to apply, or null for default
     * @param styles     the list of styles to apply, or null for none
     *
     * @return a new formatting components instance
     *
     * @since 0.0.10
     */
    public static FormattingComponents of(@Nullable ANSI.Foreground foreground, @Nullable ANSI.Background background, @Nullable List<ANSI.Style> styles) {
        return new FormattingComponents(foreground, background, styles);
    }

    /**
     * Wraps the given input string with ANSI escape codes defined by these components.
     *
     * @param input the string to format
     *
     * @return the formatted string with a trailing reset code
     *
     * @since 0.0.10
     */
    public String format(String input) {
        StringBuilder builder = new StringBuilder();

        if (styles != null) {
            for (ANSI.Style style : styles) {
                builder.append(style.code);
            }
        }

        if (foreground != null) builder.append(foreground.code);
        if (background != null) builder.append(background.code);

        builder.append(input);
        builder.append(ANSI.Style.RESET.code);
        return builder.toString();
    }

    /**
     * Returns the foreground color defined by these components.
     *
     * @return the foreground color
     *
     * @since 0.0.10
     */
    public @Nullable ANSI.Foreground getForeground() {
        return foreground;
    }

    /**
     * Returns the background color defined by these components.
     *
     * @return the background color
     *
     * @since 0.0.10
     */
    public @Nullable ANSI.Background getBackground() {
        return background;
    }

    /**
     * Returns the list of text styles defined by these components.
     *
     * @return the styles list
     *
     * @since 0.0.10
     */
    public @Nullable List<ANSI.Style> getStyles() {
        return styles;
    }

}
