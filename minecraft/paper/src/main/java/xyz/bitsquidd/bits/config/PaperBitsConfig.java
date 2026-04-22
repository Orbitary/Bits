/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.log.PaperBitsLogger;
import xyz.bitsquidd.bits.mc.command.PaperBitsCommandManager;
import xyz.bitsquidd.bits.mc.permission.Permission;
import xyz.bitsquidd.bits.paper.format.CommonPaperFormatters;
import xyz.bitsquidd.bits.paper.util.bukkit.runnable.Runnables;

import java.util.Locale;

public abstract class PaperBitsConfig extends MinecraftBitsConfig {
    private final JavaPlugin plugin;

    public PaperBitsConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static PaperBitsConfig get() {
        return (PaperBitsConfig)BitsConfig.get();
    }


    @Override
    public void startup() {
        super.startup();
        CommonPaperFormatters.init();
    }


    public final JavaPlugin plugin() {
        return plugin;
    }

    @Override
    protected PaperBitsLogger createLogger() {
        return new PaperBitsLogger(plugin, Logger.LogFlags.defaultFlags());
    }

    @Override
    public final boolean hasPermission(Audience audience, Permission permission) {
        if (audience instanceof CommandSender commandSender) {
            return commandSender.hasPermission(permission.toString());
        } else {
            return false;
        }
    }

    @Override
    public final void registerPermission(Permission permission) {
        Bukkit.getPluginManager().addPermission(new org.bukkit.permissions.Permission(permission.toString()));
    }

    @Override
    public final Locale getLocale(Audience audience) {
        if (audience instanceof Player player) return player.locale();
        return Locale.getDefault();
    }

    @Override
    public final Audience getAll() {
        return Audience.audience(Bukkit.getOnlinePlayers());
    }

    @Override
    public final void runLater(Runnable runnable, long delayMs) {
        Runnables.later(runnable, delayMs / 50); // Convert ms to ticks
    }

    @Override
    public final void runLaterAsync(Runnable runnable, long delayMs) {
        Runnables.buildLater(runnable, delayMs / 50).async().run(); // Convert ms to ticks
    }

    @Override
    protected PaperBitsCommandManager createCommandManager() {
        return new PaperBitsCommandManager();
    }

    @Override
    public final PaperBitsCommandManager getCommandManager() {
        return (PaperBitsCommandManager)super.getCommandManager();
    }


}
