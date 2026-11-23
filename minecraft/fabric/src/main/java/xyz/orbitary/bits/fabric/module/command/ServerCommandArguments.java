/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.fabric.module.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xyz.orbitary.bits.mc.module.command.BrigadierArgument;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * The argument types Fabric server commands accept out of the box: {@code ServerPlayer} and
 * {@code Collection<ServerPlayer>} (comma separated names, or {@code *} for everyone).
 *
 * @since 0.1.12
 */
public final class ServerCommandArguments {

    private ServerCommandArguments() {}

    public static void registerDefaults(BrigadierArguments<CommandSourceStack> arguments) {
        arguments.register(ServerPlayer.class, BrigadierArgument.words(
          (context, name) -> player(context.getSource().getServer(), context.getArgument(name, String.class)),
          ServerCommandArguments::playerNames,
          false
        ));

        arguments.registerCollection(ServerPlayer.class, BrigadierArgument.words(
          (context, name) -> players(context.getSource().getServer(), context.getArgument(name, String.class)),
          ServerCommandArguments::playerNames,
          true
        ));
    }

    private static ServerPlayer player(MinecraftServer server, String name) throws CommandSyntaxException {
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        if (player == null) throw BrigadierArgument.error("Player '" + name + "' is not online.");
        return player;
    }

    private static List<ServerPlayer> players(MinecraftServer server, String input) throws CommandSyntaxException {
        if (input.equals("*")) return List.copyOf(server.getPlayerList().getPlayers());

        List<ServerPlayer> players = new ArrayList<>();
        for (String name : input.split(",")) {
            ServerPlayer player = player(server, name);
            if (!players.contains(player)) players.add(player);
        }
        return players;
    }

    private static Collection<String> playerNames(CommandSourceStack source) {
        return Arrays.asList(source.getServer().getPlayerNames());
    }

}
