/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.velocity.module.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import xyz.orbitary.bits.mc.module.command.BrigadierArgument;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;


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
        arguments.register(Player.class, word(
          (context, name) -> proxy.getPlayer(context.getArgument(name, String.class))
            .orElseThrow(() -> BrigadierArgument.error("That player is not online.")),
          () -> playerNames(proxy)
        ));

        arguments.registerCollection(Player.class, word(
          (context, name) -> players(proxy, context.getArgument(name, String.class)),
          () -> playerNames(proxy),
          true
        ));

        arguments.register(RegisteredServer.class, word(
          (context, name) -> proxy.getServer(context.getArgument(name, String.class))
            .orElseThrow(() -> BrigadierArgument.error("That server does not exist.")),
          () -> proxy.getAllServers().stream().map(server -> server.getServerInfo().getName()).toList()
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

    private static BrigadierArgument<CommandSource> word(BrigadierArgument.Reader<CommandSource> reader, Supplier<Collection<String>> values) {
        return word(reader, values, false);
    }

    /**
     * @param list whether the input is a comma separated list, so suggestions complete the last entry
     */
    private static BrigadierArgument<CommandSource> word(BrigadierArgument.Reader<CommandSource> reader, Supplier<Collection<String>> values, boolean list) {
        return new BrigadierArgument<>() {
            @Override
            public ArgumentType<?> argumentType() {
                return StringArgumentType.word();
            }

            @Override
            public Object read(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
                return reader.read(context, name);
            }

            @Override
            public SuggestionProvider<CommandSource> suggestions() {
                return (context, builder) -> {
                    String input = builder.getRemaining();
                    int start = list ? input.lastIndexOf(',') + 1 : 0;
                    String prefix = input.substring(start).toLowerCase(Locale.ROOT);
                    SuggestionsBuilder entry = builder.createOffset(builder.getStart() + start);
                    values.get().stream()
                      .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix))
                      .forEach(entry::suggest);
                    return entry.buildFuture();
                };
            }
        };
    }

}
