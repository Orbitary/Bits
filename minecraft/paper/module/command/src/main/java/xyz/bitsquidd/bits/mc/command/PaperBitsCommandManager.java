/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import xyz.bitsquidd.bits.lifecycle.manager.BitsModule;
import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandBuilder;


@AutoService(BitsModule.class)
public class PaperBitsCommandManager extends BitsCommandManager<CommandSourceStack> {

    public static PaperBitsCommandManager get() {
        return BitsCommandManager.get();
    }

    @Override
    protected PaperBitsArgumentRegistry initialiseArgumentRegistry() {
        return new PaperBitsArgumentRegistry();
    }

    @Override
    protected PaperBitsRequirementRegistry initialiseRequirementRegistry() {
        return new PaperBitsRequirementRegistry();
    }

    @Override
    public PaperBitsCommandContext createContext(CommandContext<CommandSourceStack> brigadierContext) {
        return new PaperBitsCommandContext(brigadierContext);
    }

    @Override
    public PaperBitsCommandSourceContext createSourceContext(CommandSourceStack sourceStack) {
        return new PaperBitsCommandSourceContext(sourceStack);
    }

    @Override
    protected void enableAllCommands() {
        CommandDispatcher<CommandSourceStack> dispatcher = MinecraftServer.getServer().getCommands().getDispatcher();

        getAllCommands().build().forEach(this::registerCommand);
        getRegisteredCommands()
          .forEach(bitsCommand -> {
              brigadierTreeGenerator.createNodes(new BitsCommandBuilder(bitsCommand))
                .forEach(node -> {
                    dispatcher.getRoot().removeCommand(node.getName());
                    dispatcher.getRoot().addChild(node);
                });
              bitsCommand.onRegister();
          });

        Logger.success("Registered " + getRegisteredCommands().size() + " commands with Paper");
    }

}
