/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.impl.bossbar;

import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableState;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.data.BossBarColor;
import xyz.bitsquidd.bits.mc.sendable.impl.bossbar.data.BossBarOverlay;
import xyz.bitsquidd.bits.wrapper.Percentage;


public abstract class AbstractBossbar extends Sendable {


    @Override
    protected BossbarConfig.Builder createConfig() {
        return new BossbarConfig.Builder();
    }

    @Override
    public final BossbarConfig config() {
        return (BossbarConfig)super.config();
    }


    public abstract Component content(SendableState state);


    public BossBarOverlay overlay(SendableState state) {
        return config().defaultOverlay;
    }

    public BossBarColor color(SendableState state) {
        return config().defaultColor;
    }

    public Percentage progress(SendableState state) {
        return config().defaultProgress;
    }


}
