/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;
import xyz.orbitary.bits.mc.module.command.CommandErrors;
import xyz.orbitary.bits.mc.module.command.BrigadierCommandBuilder;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;

import java.util.List;
import java.util.function.Consumer;


/**
 * Registers every {@link xyz.orbitary.bits.module.command.Command} in the classpath with the Paper dispatcher.
 *
 * @since 0.1.6
 */
public final class PaperCommandManager implements BitsModule {
    private final Consumer<? super BrigadierArguments<CommandSourceStack>> customArguments;

    /**
     * @param customArguments adds argument types beyond the Paper defaults, such as a plugin's own types
     *
     * @since 0.1.6
     */
    public PaperCommandManager(Consumer<? super BrigadierArguments<CommandSourceStack>> customArguments) {
        this.customArguments = customArguments;
    }

    @Override
    public void startup() {
        BrigadierArguments<CommandSourceStack> arguments = BrigadierArguments.withDefaults();
        PaperArguments.registerDefaults(arguments);
        customArguments.accept(arguments);

        List<CommandDefinition> definitions = CommandScanner.scanAll(PaperCommandContext.class::equals);
        BrigadierCommandBuilder<CommandSourceStack> builder = new BrigadierCommandBuilder<>(
          arguments,
          context -> new PaperCommandContext(context.getSource()),
          (source, error) -> source.getBukkitSender().sendMessage(Component.text(CommandErrors.message(error), NamedTextColor.RED)),
          runnable -> Bits.get().runLaterAsync(runnable, 0)
        );

        CommandDispatcher<CommandSourceStack> dispatcher = MinecraftServer.getServer().getCommands().getDispatcher();
        List<LiteralArgumentBuilder<CommandSourceStack>> roots = builder.build(definitions);
        for (LiteralArgumentBuilder<CommandSourceStack> root : roots) {
            dispatcher.getRoot().removeCommand(root.getLiteral());
            dispatcher.register(root);
        }
        Logger.success("Registered " + definitions.size() + " commands with Paper");
    }

}
