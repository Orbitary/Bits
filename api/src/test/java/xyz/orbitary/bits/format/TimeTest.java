/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.format;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeTest {

    @Test
    void ticksConvertToAndFromDurations() {
        assertEquals(20, Time.TO_TICKS(Duration.ofSeconds(1)));
        assertEquals(0, Time.TO_TICKS(Duration.ofMillis(49)));
        assertEquals(Duration.ofSeconds(1), Time.FROM_TICKS(20));
    }

    @Test
    void secondsShowOnlyNeededDecimalPlaces() {
        assertEquals("5s", Time.FORMAT_SECS(Duration.ofSeconds(5)));
        assertEquals("5.1s", Time.FORMAT_SECS(Duration.ofMillis(5100)));
        assertEquals("5.5s", Time.FORMAT_SECS(Duration.ofMillis(5500)));
        assertEquals("5.12s", Time.FORMAT_SECS(Duration.ofMillis(5120)));
    }

    @Test
    void negativeDurationsKeepTheirSign() {
        assertEquals("-5.1s", Time.FORMAT_SECS(Duration.ofMillis(-5100)));
        assertEquals("-05:07", Time.FORMAT_CLOCK_MMSS(Duration.ofSeconds(-307)));
        assertEquals("-5m 30s", Time.FORMAT_MINS_ROUNDED(Duration.ofSeconds(-330)));
    }

    @Test
    void roundedSecondsRoundHalfUp() {
        assertEquals("5s", Time.FORMAT_SECS_ROUNDED(Duration.ofMillis(5499)));
        assertEquals("6s", Time.FORMAT_SECS_ROUNDED(Duration.ofMillis(5500)));
    }

    @Test
    void minutesOmitSecondsWhenZero() {
        assertEquals("5m", Time.FORMAT_MINS_ROUNDED(Duration.ofSeconds(300)));
        assertEquals("5m 30s", Time.FORMAT_MINS_ROUNDED(Duration.ofSeconds(330)));
    }

    @Test
    void clockFormatsPadToTwoDigits() {
        assertEquals("05:07", Time.FORMAT_CLOCK_MMSS(Duration.ofSeconds(307)));
        assertEquals("00:45", Time.FORMAT_CLOCK_MMSS(Duration.ofSeconds(45)));
    }

    @Test
    void nonZeroMinutesDropLeadingZero() {
        assertEquals("5:07", Time.FORMAT_CLOCK_NONZERO_MINUTES(Duration.ofSeconds(307)));
        assertEquals("0:45", Time.FORMAT_CLOCK_NONZERO_MINUTES(Duration.ofSeconds(45)));
    }

    @Test
    void tickClockQuantisesMillisecondsToFiftyMillisecondSteps() {
        assertEquals("07:15", Time.FORMAT_CLOCK_SSMSMS(Duration.ofMillis(7150)));
        assertEquals("00:95", Time.FORMAT_CLOCK_SSMSMS(Duration.ofMillis(999)));
        assertEquals("05:07:15", Time.FORMAT_CLOCK_MMSSMSMS(Duration.ofMillis(307150)));
    }

    @Test
    void unroundedClockKeepsTrueMilliseconds() {
        assertEquals("05:07:153", Time.FORMAT_CLOCK_MMSSMSMS_NOROUND(Duration.ofMillis(307153)));
    }

}
