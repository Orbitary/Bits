/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import net.fabricmc.loader.api.FabricLoader;

import xyz.bitsquidd.bits.mc.command.provider.BitsCommandProvider;
import xyz.bitsquidd.bits.wrapper.collection.AddableSet;


public abstract class FabricBitsCommandManager<T> extends BitsCommandManager<T> {
    public static final String COMMAND_INSTANCE_ENTRYPOINT = "bits:command";

    //    @Override
    //    protected void enableAllCommands() {
    //        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
    //            getAllCommands().build().forEach(this::registerCommand);
    //            getRegisteredCommands().forEach(bitsCommand -> {
    //                brigadierTreeGenerator.createNodes(new BitsCommandBuilder(bitsCommand))
    //                  .forEach(node -> dispatcher.register(node.createBuilder()));
    //                bitsCommand.onRegister();
    //            });
    //        });
    //    }

    /**
     * Fabric mods must use the {@code COMMAND_INSTANCE_ENTRYPOINT} entrypoint to provide commands.
     * This method retrieves all commands from that entrypoint.
     */
    @Override
    protected AddableSet<BitsCommand> getAllCommands() {
        return super.getAllCommands().addAll(FabricLoader.getInstance()
          .getEntrypoints(COMMAND_INSTANCE_ENTRYPOINT, BitsCommandProvider.class)
          .stream()
          .flatMap(provider -> provider.getCommands().build().stream())
          .toList()
        );
    }

}