/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.velocity.module.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import xyz.orbitary.bits.mc.module.command.BrigadierArgument;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;

import java.util.ArrayList;
import java.util.List;


/**
 * The argument types Velocity commands accept out of the box: {@code Player}, {@code Collection<Player>}
 * (comma separated names, or {@code *} for everyone) and {@code RegisteredServer}. Players are suggested from the
 * whole proxy, so players on any backend appear, whatever their state there.
 *
 * @since 0.1.8
 */
final class VelocityArguments {

    private VelocityArguments() {}

    static void registerDefaults(BrigadierArguments<CommandSource> arguments, ProxyServer proxy) {
        arguments.register(Player.class, BrigadierArgument.words(
          (context, name) -> proxy.getPlayer(context.getArgument(name, String.class))
            .orElseThrow(() -> BrigadierArgument.error("That player is not online.")),
          source -> playerNames(proxy),
          false
        ));

        arguments.registerCollection(Player.class, BrigadierArgument.words(
          (context, name) -> players(proxy, context.getArgument(name, String.class)),
          source -> playerNames(proxy),
          true
        ));

        arguments.register(RegisteredServer.class, BrigadierArgument.words(
          (context, name) -> proxy.getServer(context.getArgument(name, String.class))
            .orElseThrow(() -> BrigadierArgument.error("That server does not exist.")),
          source -> proxy.getAllServers().stream().map(server -> server.getServerInfo().getName()).toList(),
          false
        ));
    }

    private static List<Player> players(ProxyServer proxy, String input) throws CommandSyntaxException {
        if (input.equals("*")) return List.copyOf(proxy.getAllPlayers());

        List<Player> players = new ArrayList<>();
        for (String username : input.split(",")) {
            Player player = proxy.getPlayer(username).orElseThrow(() -> BrigadierArgument.error("Player '" + username + "' is not online."));
            if (!players.contains(player)) players.add(player);
        }
        return players;
    }

    private static List<String> playerNames(ProxyServer proxy) {
        return proxy.getAllPlayers().stream().map(Player::getUsername).toList();
    }

}
