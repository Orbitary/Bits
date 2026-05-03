/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config;

import net.kyori.adventure.audience.Audience;

import xyz.bitsquidd.bits.mc.permission.Permission;

import java.util.Locale;

public abstract class MinecraftBitsConfig extends BitsConfig {
    private boolean paused = false;

    protected MinecraftBitsConfig() {
        super();
    }

    public static MinecraftBitsConfig get() {
        return (MinecraftBitsConfig)BitsConfig.get();
    }


    //region Sendable Utils
    public abstract boolean hasPermission(Audience audience, Permission permission);

    public abstract void registerPermission(Permission permission);

    public abstract Locale getLocale(Audience audience);

    public abstract Audience getAll();
    //endregion


    //region Runnable API
    public boolean isPaused() {
        return paused;
    }

    public void pause(boolean paused) {
        this.paused = paused;
    }
    //endregion
    

}
