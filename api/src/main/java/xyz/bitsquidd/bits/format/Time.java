/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.format;

import java.time.Duration;


public final class Time {
    private Time() {}

    public enum ClockType {
        COUNTDOWN,
        COUNTUP,
        TOTAL,
        ;
    }

    /**
     * Converts a Duration to ticks (1/20th of a second).
     */
    public static int TO_TICKS(Duration duration) {
        return (int)(duration.toMillis() / 50);
    }

    /**
     * Converts ticks (1/20th of a second) to a Duration.
     */
    public static Duration FROM_TICKS(long ticks) {
        return Duration.ofMillis(ticks * 50L);
    }


    /**
     * The sign prefix ("-" or "") for a duration, to be applied to its magnitude - Duration's part-extraction
     * methods (toSecondsPart, toMinutesPart, ...) carry the sign inconsistently across parts for negative
     * durations (nanos are always non-negative, only seconds carries the sign), so every formatter below
     * works on the absolute magnitude and re-applies the sign itself.
     */
    private static String sign(Duration duration) {
        return duration.isNegative() ? "-" : "";
    }

    private static Duration magnitude(Duration duration) {
        return duration.isNegative() ? duration.negated() : duration;
    }


    /**
     * Formats a duration into a generic time string with seconds and appropriate decimal places.
     * e.g. "5s", "5.1s", "5.12s", "-5.1s"
     */
    public static String FORMAT_SECS(Duration duration) {
        String sign = sign(duration);
        Duration magnitude = magnitude(duration);
        long seconds = magnitude.toSeconds();
        int millis = magnitude.toMillisPart();

        if (millis == 0) {
            return sign + seconds + "s";
        } else if (millis % 100 == 0) {
            return String.format("%s%d.%1ds", sign, seconds, millis / 100);
        } else {
            return String.format("%s%d.%02ds", sign, seconds, millis / 10);
        }
    }

    /**
     * Formats a duration into a generic time string with seconds, rounded to the nearest second.
     * e.g. "5s", "6s"
     */
    public static String FORMAT_SECS_ROUNDED(Duration duration) {
        long totalSeconds = duration.toSeconds();
        int millis = duration.toMillisPart();

        if (millis >= 500) totalSeconds += 1;

        return totalSeconds + "s";
    }

    /**
     * Formats a duration into a generic time string with minutes and seconds.
     * e.g. "5m", "5m 30s", "-5m 30s"
     */
    public static String FORMAT_MINS_ROUNDED(Duration duration) {
        String sign = sign(duration);
        Duration magnitude = magnitude(duration);
        long minutes = magnitude.toMinutes();
        int seconds = magnitude.toSecondsPart();

        if (seconds == 0) {
            return sign + minutes + "m";
        } else {
            return String.format("%s%dm %ds", sign, minutes, seconds);
        }
    }


    /**
     * Formats a duration into a clock-style time string (MM:SS).
     * e.g. "05:07", "00:45" or "-05:07"
     */
    public static String FORMAT_CLOCK_MMSS(Duration duration) {
        String sign = sign(duration);
        Duration magnitude = magnitude(duration);
        int secondsPart = magnitude.toSecondsPart();
        int minutesPart = magnitude.toMinutesPart();

        return String.format("%s%02d:%02d", sign, minutesPart, secondsPart);
    }

    /**
     * Formats a duration into a clock-style time string (SS:msms), where msms is the elapsed 50ms tick within
     * the second shown as a 2-digit 00-95 step (not true milliseconds).
     * e.g. "07:15" (7 seconds, 3 ticks in) or "-07:15"
     */
    public static String FORMAT_CLOCK_SSMSMS(Duration duration) {
        String sign = sign(duration);
        Duration magnitude = magnitude(duration);
        int secondsPart = magnitude.toSecondsPart();
        int millisPart = magnitude.toMillisPart();
        millisPart = Math.min(95, (Math.round(millisPart / 50.0f) * 5));

        return String.format("%s%02d:%02d", sign, secondsPart, millisPart);
    }

    /**
     * Formats a duration into a clock-style time string (MM:SS:msms), where msms is the elapsed 50ms tick
     * within the second shown as a 2-digit 00-95 step (not true milliseconds).
     * e.g. "05:07:15" or "00:45:05" or "-05:07:15"
     */
    public static String FORMAT_CLOCK_MMSSMSMS(Duration duration) {
        String sign = sign(duration);
        Duration magnitude = magnitude(duration);
        int secondsPart = magnitude.toSecondsPart();
        int minutesPart = magnitude.toMinutesPart();
        int millisPart = magnitude.toMillisPart();
        millisPart = Math.min(95, (Math.round(millisPart / 50.0f) * 5));

        return String.format("%s%02d:%02d:%02d", sign, minutesPart, secondsPart, millisPart);
    }

    /**
     * Formats a duration into a clock-style time string (MM:SS:MS) with true, unrounded milliseconds -
     * unlike {@link #FORMAT_CLOCK_MMSSMSMS}, this does not quantize to the 50ms tick grid.
     * e.g. "05:07:153" or "00:45:008" or "-05:07:153"
     */
    public static String FORMAT_CLOCK_MMSSMSMS_NOROUND(Duration duration) {
        String sign = sign(duration);
        Duration magnitude = magnitude(duration);
        int secondsPart = magnitude.toSecondsPart();
        int minutesPart = magnitude.toMinutesPart();
        int millisPart = magnitude.toMillisPart();

        return String.format("%s%02d:%02d:%03d", sign, minutesPart, secondsPart, millisPart);
    }

    /**
     * Formats a duration into a clock-style time string (M:SS), allowing for non-zero minutes without leading zero.
     * e.g. "5:07", "0:45" or "-5:07"
     */
    public static String FORMAT_CLOCK_NONZERO_MINUTES(Duration duration) {
        String sign = sign(duration);
        Duration magnitude = magnitude(duration);
        int secondsPart = magnitude.toSecondsPart();
        int minutesPart = (int)magnitude.toMinutes();

        if (minutesPart > 0) {
            return String.format("%s%d:%02d", sign, minutesPart, secondsPart);
        } else {
            return String.format("%s0:%02d", sign, secondsPart);
        }
    }

}