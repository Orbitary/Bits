/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits;

import net.dv8tion.jda.api.JDA;

import xyz.bitsquidd.bits.log.BasicLogger;
import xyz.bitsquidd.bits.log.Logger;


public class BitsDiscord extends Bits{
    private final JDA jda;
    private final org.slf4j.Logger slf4j;

    public BitsDiscord(JDA jda, org.slf4j.Logger slf4j) {
        this.jda = jda;
        this.slf4j = slf4j;
    }

    public static BitsDiscord get() {
        return (BitsDiscord)Bits.get();
    }


    public static JDA jda() {
        return get().jda;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        jda.shutdown();
    }


    @Override
    protected Logger createLogger() {
        return new BasicLogger(slf4j, Logger.LogFlags.defaultFlags());
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

}
