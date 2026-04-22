/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.Audience;

import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.log.VelocityBitsLogger;
import xyz.bitsquidd.bits.mc.command.VelocityBitsCommandManager;
import xyz.bitsquidd.bits.mc.permission.Permission;
import xyz.bitsquidd.bits.velocity.util.velocity.runnable.Tasks;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class VelocityBitsConfig extends MinecraftBitsConfig {
    private final Object plugin;
    private final ProxyServer server;
    private final org.slf4j.Logger slf4j;

    public VelocityBitsConfig(ProxyServer server, Object plugin, org.slf4j.Logger slf4j) {
        this.server = server;
        this.plugin = plugin;
        this.slf4j = slf4j;
    }

    public static VelocityBitsConfig get() {
        return (VelocityBitsConfig)BitsConfig.get();
    }


    public final Object getPlugin() {
        return plugin;
    }

    public final ProxyServer getServer() {
        return server;
    }

    @Override
    protected Logger createLogger() {
        return new VelocityBitsLogger(slf4j, Logger.LogFlags.defaultFlags());
    }

    @Override
    public final boolean hasPermission(Audience audience, Permission permission) {
        if (audience instanceof CommandSource commandSource) {
            return commandSource.hasPermission(permission.toString());
        } else {
            return false;
        }
    }

    @Override
    public final void registerPermission(Permission permission) {
        // Unimplemented in Velocity
    }

    @Override
    public final Locale getLocale(Audience audience) {
        if (audience instanceof Player player) {
            Locale locale = player.getEffectiveLocale();
            if (locale != null) player.getEffectiveLocale();
        }
        return Locale.getDefault();
    }

    @Override
    public final Audience getAll() {
        return Audience.audience(getServer().getAllPlayers());
    }

    @Override
    public final void runLater(Runnable runnable, long delayMs) {
        Tasks.builder(runnable).delay(delayMs, TimeUnit.MILLISECONDS).schedule();
    }

    @Override
    public final void runLaterAsync(Runnable runnable, long delayMs) {
        runLater(runnable, delayMs); // Velocity runs all tasks asynchronously by default.
    }

    @Override
    protected VelocityBitsCommandManager createCommandManager() {
        return new VelocityBitsCommandManager();
    }

    @Override
    public final VelocityBitsCommandManager getCommandManager() {
        return (VelocityBitsCommandManager)super.getCommandManager();
    }

}
