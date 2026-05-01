/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandBuilder;

public final class FabricClientBitsCommandManager extends FabricBitsCommandManager<FabricClientCommandSource> {

    @Override
    protected FabricClientBitsArgumentRegistry initialiseArgumentRegistry() {
        return new FabricClientBitsArgumentRegistry();
    }

    @Override
    protected FabricClientBitsRequirementRegistry initialiseRequirementRegistry() {
        return new FabricClientBitsRequirementRegistry();
    }

    @Override
    public FabricClientBitsCommandContext createContext(CommandContext<FabricClientCommandSource> brigadierContext) {
        return new FabricClientBitsCommandContext(brigadierContext);
    }

    @Override
    public FabricClientBitsCommandSourceContext createSourceContext(FabricClientCommandSource sourceStack) {
        return new FabricClientBitsCommandSourceContext(sourceStack);
    }


    @Override
    protected void enableAllCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            getAllCommands().build().forEach(this::registerCommand);
            getRegisteredCommands().forEach(bitsCommand -> {
                brigadierTreeGenerator.createNodes(new BitsCommandBuilder(bitsCommand))
                  .forEach(node -> dispatcher.getRoot().addChild(node));
                bitsCommand.onRegister();
            });
        });

        Logger.success("Registered " + getRegisteredCommands().size() + " commands with Fabric");
    }

}
