/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.Audience;
import org.bstats.velocity.Metrics;

import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.mc.permission.Permission;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;
import xyz.bitsquidd.bits.velocity.util.velocity.runnable.Tasks;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class BitsVelocity extends BitsMinecraft {
    private static final int BSTATS_ID = 31211;

    private final Object plugin;
    private final ProxyServer server;
    private final Path dataDir;


    protected BitsVelocity(Object plugin, Injector injector) {
        this.plugin = plugin;
        this.server = injector.getInstance(ProxyServer.class);
        this.dataDir = injector.getInstance(Key.get(Path.class, DataDirectory.class));
    }

    public static BitsVelocity get() {
        return (BitsVelocity)Bits.get();
    }

    public static BitsVelocity generic(Object plugin, Injector injector) {
        return new BitsVelocity(plugin, injector) {};
    }


    @Override
    public void startup() {
        super.startup();
        ReflectionUtils.Instance.tryCreate(Metrics.class, plugin, server, Logger.get(), dataDir, BSTATS_ID);
    }


    public final Object getPlugin() {
        return plugin;
    }

    public final ProxyServer getServer() {
        return server;
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

}
