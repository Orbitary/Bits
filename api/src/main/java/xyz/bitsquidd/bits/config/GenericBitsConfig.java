/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config;

import org.slf4j.LoggerFactory;

import xyz.bitsquidd.bits.log.BasicLogger;
import xyz.bitsquidd.bits.log.Logger;


/**
 * Platform-independent implementation of BitsConfig, used for any platform that doesn't have specific requirements.
 */
public final class GenericBitsConfig extends Bits {
    private final String platformName;

    public GenericBitsConfig(String platformName) {
        this.platformName = platformName;
    }

    @Override
    protected Logger createLogger() {
        return new BasicLogger(LoggerFactory.getLogger(platformName), Logger.LogFlags.defaultFlags());
    }

    @Override
    public void runLater(Runnable runnable, long delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                runnable.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void runLaterAsync(Runnable runnable, long delayMs) {
        runLater(runnable, delayMs); // All threads are "async"
    }

}
