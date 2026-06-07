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
     * Formats a duration into a generic time string with seconds and appropriate decimal places.
     * e.g. "5s", "5.1s", "5.12s"
     */
    public static String FORMAT_SECS(Duration duration) {
        long seconds = duration.toSeconds();
        int millis = duration.toMillisPart();

        if (millis == 0) {
            return seconds + "s";
        } else if (millis % 100 == 0) {
            return String.format("%d.%1ds", seconds, millis / 100);
        } else {
            return String.format("%d.%02ds", seconds, millis / 10);
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
     * e.g. "5m", "5m 30s"
     */
    public static String FORMAT_MINS_ROUNDED(Duration duration) {
        long minutes = duration.toMinutes();
        int seconds = duration.toSecondsPart();

        if (seconds == 0) {
            return minutes + "m";
        } else {
            return String.format("%dm %ds", minutes, seconds);
        }
    }


    /**
     * Formats a duration into a clock-style time string (MM:SS).
     * e.g. "05:07" or "00:45"
     */
    public static String FORMAT_CLOCK_MMSS(Duration duration) {
        int secondsPart = duration.toSecondsPart();
        int minutesPart = duration.toMinutesPart();

        return String.format("%02d:%02d", minutesPart, secondsPart);
    }

    /**
     * Formats a duration into a clock-style time string (SS:SSMS).
     * e.g. "05:07" or "00:45"
     */
    public static String FORMAT_CLOCK_SSMSMS(Duration duration) {
        int secondsPart = duration.toSecondsPart();
        int millisPart = duration.toMillisPart();
        millisPart = Math.min(95, (Math.round(millisPart / 50.0f) * 5));

        return String.format("%02d:%02d", secondsPart, millisPart);
    }

    /**
     * Formats a duration into a clock-style time string (MM:SS:MSMS).
     * e.g. "05:07:15" or "00:45:05"
     */
    public static String FORMAT_CLOCK_MMSSMSMS(Duration duration) {
        int secondsPart = duration.toSecondsPart();
        int minutesPart = duration.toMinutesPart();
        int millisPart = duration.toMillisPart();
        millisPart = Math.min(95, (Math.round(millisPart / 50.0f) * 5));

        return String.format("%02d:%02d:%02d", minutesPart, secondsPart, millisPart);
    }

    /**
     * Formats a duration into a clock-style time string (M:SS), allowing for non-zero minutes without leading zero.
     * e.g. "5:07" or "0:45"
     */
    public static String FORMAT_CLOCK_NONZERO_MINUTES(Duration duration) {
        int secondsPart = duration.toSecondsPart();
        int minutesPart = (int)duration.toMinutes();

        if (minutesPart > 0) {
            return String.format("%d:%02d", minutesPart, secondsPart);
        } else {
            return String.format("0:%02d", secondsPart);
        }
    }

}