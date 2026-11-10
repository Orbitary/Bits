/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.log;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.log.pretty.ANSI;
import xyz.orbitary.bits.mc.permission.Permission;

import java.io.File;


public class PaperBitsLogger extends BasicLogger {
    private final org.apache.logging.log4j.Logger output;
    private final org.apache.logging.log4j.@Nullable Logger serverLog;

    public PaperBitsLogger(JavaPlugin plugin, LogFlags flags) {
        super(plugin.getSLF4JLogger(), flags);
        this.output = Log4jConsole.create("Bits", new File(plugin.getDataFolder(), "logs/bits.log"));
        this.serverLog = Log4jConsole.hostFile("Bits");
    }

    @Override
    protected void writeLine(final String line) {
        output.info(line);
        if (serverLog != null) serverLog.info(ANSI.strip(line));
    }

    private void notifyStaff(LogData logData) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (Permission.of("bits.log." + logData.type().id() + ".notify").hasPermission(player)) {
                notifyPlayer(logData, player);
            }
        });
    }

    protected void notifyPlayer(LogData logData, Player player) {
        // Here you can customise how the log is sent to a player.
    }

    @Override
    protected void onLog(LogData logData) {
        super.onLog(logData);
        notifyStaff(logData);

    }

}