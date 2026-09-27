/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.log.pretty;

import java.io.Console;
import java.util.regex.Pattern;

/**
 * Provides a comprehensive set of ANSI escape codes for styling console output.
 *
 * @since 0.0.10
 */
public class ANSI {
    private static final Pattern ESCAPE_SEQUENCE = Pattern.compile("\\x1B\\[[0-9;]*m");

    /**
     * System property overriding {@link #isSupported()} detection, accepting {@code true} or {@code false}.
     *
     * @since 0.0.23
     */
    public static final String OVERRIDE_PROPERTY = "bits.ansi";

    private static final boolean SUPPORTED = detectSupport();

    public final String code;

    private ANSI(String code) {
        this.code = code;
    }

    /**
     * Whether the current output stream is expected to render ANSI escape codes.
     * <p>
     * Detection is a heuristic, resolved once at class initialisation, in order of precedence:
     * the {@value #OVERRIDE_PROPERTY} system property, the {@code NO_COLOR} environment variable
     * (see <a href="https://no-color.org">no-color.org</a>), then whether the process is attached
     * to a terminal. Redirected output, IDE consoles and log files typically report {@code false}.
     *
     * @since 0.0.23
     */
    public static boolean isSupported() {
        return SUPPORTED;
    }

    /**
     * Removes every ANSI escape code from a string, leaving its visible text intact.
     *
     * @since 0.0.23
     */
    public static String strip(String text) {
        return ESCAPE_SEQUENCE.matcher(text).replaceAll("");
    }

    /**
     * Returns the string as-is where ANSI is supported, otherwise stripped of escape codes.
     *
     * @since 0.0.23
     */
    public static String render(String text) {
        return SUPPORTED ? text : strip(text);
    }

    private static boolean detectSupport() {
        String override = System.getProperty(OVERRIDE_PROPERTY);
        if (override != null) return Boolean.parseBoolean(override);
        if (System.getenv("NO_COLOR") != null) return false;
        if ("dumb".equals(System.getenv("TERM"))) return false;

        Console console = System.console();
        if (console == null) return false;

        // Console#isTerminal is Java 22+, whereas this module targets 21. Prior to 22 a
        // non-null Console already implies a terminal; from 22 onward it no longer does.
        try {
            return (boolean)Console.class.getMethod("isTerminal").invoke(console);
        } catch (NoSuchMethodException e) {
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    public static abstract class Color extends ANSI {
        public final boolean isLight;

        protected Color(String code, boolean isLight) {
            super(code);
            this.isLight = isLight;
        }

    }

    /**
     * A collection of ANSI foreground (text) color codes.
     *
     * @since 0.0.10
     */
    public static final class Foreground extends Color {
        public static final Foreground BLACK = new Foreground("\u001B[30m", false);
        public static final Foreground RED = new Foreground("\u001B[31m", true);
        public static final Foreground GREEN = new Foreground("\u001B[32m", true);
        public static final Foreground YELLOW = new Foreground("\u001B[33m", true);
        public static final Foreground BLUE = new Foreground("\u001B[34m", true);
        public static final Foreground PURPLE = new Foreground("\u001B[35m", true);
        public static final Foreground CYAN = new Foreground("\u001B[36m", true);
        public static final Foreground WHITE = new Foreground("\u001B[37m", true);

        public static final Foreground BRIGHT_BLACK = new Foreground("\u001B[90m", false);
        public static final Foreground BRIGHT_RED = new Foreground("\u001B[91m", true);
        public static final Foreground BRIGHT_GREEN = new Foreground("\u001B[92m", true);
        public static final Foreground BRIGHT_YELLOW = new Foreground("\u001B[93m", true);
        public static final Foreground BRIGHT_BLUE = new Foreground("\u001B[94m", true);
        public static final Foreground BRIGHT_PURPLE = new Foreground("\u001B[95m", true);
        public static final Foreground BRIGHT_CYAN = new Foreground("\u001B[96m", true);
        public static final Foreground BRIGHT_WHITE = new Foreground("\u001B[97m", true);

        Foreground(String code, boolean isLight) {
            super(code, isLight);
        }

    }

    /**
     * A collection of ANSI background color codes.
     *
     * @since 0.0.10
     */
    public static final class Background extends Color {
        public static final Background BLACK = new Background("\u001B[40m", false);
        public static final Background RED = new Background("\u001B[41m", true);
        public static final Background GREEN = new Background("\u001B[42m", true);
        public static final Background YELLOW = new Background("\u001B[43m", true);
        public static final Background BLUE = new Background("\u001B[44m", true);
        public static final Background PURPLE = new Background("\u001B[45m", true);
        public static final Background CYAN = new Background("\u001B[46m", true);
        public static final Background WHITE = new Background("\u001B[47m", true);

        public static final Background BRIGHT_BLACK = new Background("\u001B[100m", false);
        public static final Background BRIGHT_RED = new Background("\u001B[101m", true);
        public static final Background BRIGHT_GREEN = new Background("\u001B[102m", true);
        public static final Background BRIGHT_YELLOW = new Background("\u001B[103m", true);
        public static final Background BRIGHT_BLUE = new Background("\u001B[104m", true);
        public static final Background BRIGHT_PURPLE = new Background("\u001B[105m", true);
        public static final Background BRIGHT_CYAN = new Background("\u001B[106m", true);
        public static final Background BRIGHT_WHITE = new Background("\u001B[107m", true);


        Background(String code, boolean isLight) {
            super(code, isLight);
        }

    }

    /**
     * A collection of ANSI text style escape codes (e.g., Bold, Italic).
     *
     * @since 0.0.10
     */
    public static final class Style extends ANSI {
        public static final Style RESET = new Style("\u001B[0m");
        public static final Style BOLD = new Style("\u001B[1m");
        public static final Style DIM = new Style("\u001B[2m");
        public static final Style ITALIC = new Style("\u001B[3m");
        public static final Style UNDERLINE = new Style("\u001B[4m");

        public Style(String code) {
            super(code);
        }

    }

}
