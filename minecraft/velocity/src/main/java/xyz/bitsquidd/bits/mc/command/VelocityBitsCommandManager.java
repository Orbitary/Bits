/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;

import xyz.bitsquidd.bits.config.VelocityBitsConfig;
import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.mc.command.requirement.BitsRequirementRegistry;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandBuilder;


public class VelocityBitsCommandManager extends BitsCommandManager<CommandSource> {

    public static VelocityBitsCommandManager get() {
        return BitsCommandManager.get();
    }

    @Override
    protected VelocityBitsArgumentRegistry initialiseArgumentRegistry() {
        return new VelocityBitsArgumentRegistry();
    }

    @Override
    protected BitsRequirementRegistry<CommandSource> initialiseRequirementRegistry() {
        return new VelocityBitsRequirementRegistry();
    }

    @Override
    public VelocityBitsCommandContext createContext(CommandContext<CommandSource> brigadierContext) {
        return new VelocityBitsCommandContext(brigadierContext);
    }

    @Override
    public VelocityBitsCommandSourceContext createSourceContext(CommandSource sourceStack) {
        return new VelocityBitsCommandSourceContext(sourceStack);
    }

    @Override
    protected void enableAllCommands() {
        CommandManager velocityCommandManager = VelocityBitsConfig.get().getServer().getCommandManager();

        getAllCommands().build().forEach(this::registerCommand);
        getRegisteredCommands()
          .forEach(bitsCommand -> {
              brigadierTreeGenerator.createNodes(new BitsCommandBuilder(bitsCommand))
                .forEach(node -> {
                    velocityCommandManager.register(
                      velocityCommandManager
                        .metaBuilder(node.getName())
                        .plugin(VelocityBitsConfig.get().getPlugin())
                        .build(),
                      new BrigadierCommand(node)
                    );
                });
              bitsCommand.onRegister();
          });

        Logger.success("Registered " + getRegisteredCommands().size() + " commands with Velocity");
    }

}
