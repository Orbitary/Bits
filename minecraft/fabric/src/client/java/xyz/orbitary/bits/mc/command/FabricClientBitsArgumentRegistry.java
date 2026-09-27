/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;

import xyz.orbitary.bits.mc.command.argument.BitsArgumentRegistry;
import xyz.orbitary.bits.mc.command.argument.parser.ArgumentParser;
import xyz.orbitary.bits.mc.command.provider.BitsCommandProvider;
import xyz.orbitary.bits.wrapper.collection.AddableSet;

import static xyz.orbitary.bits.mc.command.FabricBitsCommandManager.COMMAND_INSTANCE_ENTRYPOINT;


public class FabricClientBitsArgumentRegistry extends BitsArgumentRegistry<FabricClientCommandSource> {

    /**
     * Fabric mods must use the {@code COMMAND_INSTANCE_ENTRYPOINT} entrypoint to provide command parsers.
     */
    @Override
    protected AddableSet<ArgumentParser<?, ?>> initialiseParsers() {
        return super.initialiseParsers().addAll(FabricLoader.getInstance()
          .getEntrypoints(COMMAND_INSTANCE_ENTRYPOINT, BitsCommandProvider.class)
          .stream()
          .flatMap(provider -> provider.getArguments().build().stream())
          .toList()
        );
    }

}
