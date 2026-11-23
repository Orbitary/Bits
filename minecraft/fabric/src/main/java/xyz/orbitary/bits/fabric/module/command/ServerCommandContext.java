/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.fabric.module.command;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;


/**
 * The source of a Fabric server command, passed to command methods that declare it.
 *
 * <pre>{@code
 * @Command("ping")
 * public void ping(ServerCommandContext context) {
 *     context.audience().sendMessage(Component.text("Pong!"));
 * }
 * }</pre>
 *
 * @since 0.1.12
 */
public final class ServerCommandContext {

    private final CommandSourceStack source;

    /**
     * @param source the source of the executing command
     *
     * @since 0.1.12
     */
    public ServerCommandContext(CommandSourceStack source) {
        this.source = source;
    }

    /**
     * @return the Minecraft source of the command
     *
     * @since 0.1.12
     */
    public CommandSourceStack source() {
        return source;
    }

    /**
     * @return the server the command is running on
     *
     * @since 0.1.12
     */
    public MinecraftServer server() {
        return source.getServer();
    }

    /**
     * @return the sender of the command as an Adventure audience, which messages the command source
     *
     * @since 0.1.12
     */
    public Audience audience() {
        return MinecraftServerAudiences.of(server()).audience(source);
    }

    /**
     * @return the sender of the command, which must be a player
     *
     * @throws IllegalStateException if the sender is not a player
     *
     * @since 0.1.12
     */
    public ServerPlayer requirePlayer() {
        ServerPlayer player = source.getPlayer();
        if (player == null) throw new IllegalStateException("Command sender must be a player");
        return player;
    }

}
