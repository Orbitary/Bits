/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.velocity.module.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;
import xyz.orbitary.bits.mc.module.command.BrigadierCommandBuilder;
import xyz.orbitary.bits.mc.module.command.CommandErrors;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;

import java.util.List;
import java.util.function.Consumer;


/**
 * Registers every {@link xyz.orbitary.bits.module.command.Command} in the classpath with the Velocity proxy.
 *
 * @since 0.1.6
 */
public final class VelocityCommandManager implements BitsModule {

    private final ProxyServer proxy;
    private final Object plugin;
    private final Consumer<BrigadierArguments<CommandSource>> customArguments;

    /**
     * Registers the defaults and the arguments found by the reflection scanner.
     *
     * @param proxy  the proxy, used to register commands and to look up players
     * @param plugin the plugin instance the commands belong to
     *
     * @since 0.1.8
     */
    public VelocityCommandManager(ProxyServer proxy, Object plugin) {
        this(proxy, plugin, arguments -> {});
    }

    /**
     * @param proxy           the proxy, used to register commands and to look up players
     * @param plugin          the plugin instance the commands belong to
     * @param customArguments adds argument types beyond the defaults, such as a plugin's own types
     *
     * @since 0.1.6
     */
    public VelocityCommandManager(ProxyServer proxy, Object plugin, Consumer<BrigadierArguments<CommandSource>> customArguments) {
        this.proxy = proxy;
        this.plugin = plugin;
        this.customArguments = customArguments;
    }

    @Override
    public void startup() {
        CommandManager commands = proxy.getCommandManager();
        BrigadierArguments<CommandSource> arguments = BrigadierArguments.withDefaults();
        VelocityArguments.registerDefaults(arguments, proxy);
        customArguments.accept(arguments);

        List<CommandDefinition> definitions = CommandScanner.scanAll(VelocityCommandContext.class::equals);
        BrigadierCommandBuilder<CommandSource> builder = new BrigadierCommandBuilder<>(
          arguments,
          context -> new VelocityCommandContext(context.getSource()),
          (source, error) -> source.sendMessage(Component.text(CommandErrors.message(error), NamedTextColor.RED)),
          (source, node) -> source.hasPermission(node),
          runnable -> Bits.get().runLaterAsync(runnable, 0)
        );

        for (LiteralArgumentBuilder<CommandSource> root : builder.build(definitions)) {
            var node = root.build();
            commands.register(commands.metaBuilder(node.getName()).plugin(plugin).build(), new BrigadierCommand(node));
        }
        Logger.success("Registered " + definitions.size() + " commands with Velocity");
    }

}
