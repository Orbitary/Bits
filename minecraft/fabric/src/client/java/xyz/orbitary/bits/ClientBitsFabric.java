/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.log.FabricBitsLogger;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.fabric.module.command.FabricCommandManager;
import xyz.orbitary.bits.mc.module.command.CommandErrors;
import xyz.orbitary.bits.mc.permission.Permission;
import xyz.orbitary.bits.wrapper.collection.AddableList;

import java.util.Locale;


@Environment(EnvType.CLIENT)
public class ClientBitsFabric extends BitsFabric {

    protected ClientBitsFabric() {}

    public static ClientBitsFabric get() {
        return (ClientBitsFabric)Bits.get();
    }

    public static ClientBitsFabric generic() {
        new FabricBitsLogger(org.slf4j.LoggerFactory.getLogger("BitsClient"), Logger.LogFlags.defaultFlags());
        return new ClientBitsFabric();
    }


    @Override
    public void startup() {
        super.startup();
        ClientTickEvents.END_CLIENT_TICK.register(_ -> tickAll());
        ClientLifecycleEvents.CLIENT_STOPPING.register(_ -> shutdown());
    }

    @Override
    public Audience getAll() {
        // TODO have MinecraftServer interface
        // On the client there is only ever one local player.
        return MinecraftClientAudiences.of().audience();
    }

    @Override
    public Locale getLocale(Audience audience) {
        // TODO have MinecraftServer interface
        // On the client, the local player's locale comes from game options.
        return Locale.forLanguageTag(Minecraft.getInstance()
          .getLanguageManager()
          .getSelected()
        );
    }

    @Override
    public void registerPermission(Permission permission) {
        // TODO have MinecraftServer interface
        // No-op on client - permissions are a server-side concept
    }

    @Override
    public boolean hasPermission(Audience audience, Permission permission) {
        // TODO have MinecraftServer interface
        return true;
    }


    @Override
    protected AddableList<BitsModule> modules() {
        return super.modules().add(new FabricCommandManager<FabricClientCommandSource>(
          arguments -> {},
          (source, error) -> source.sendError(Component.literal(CommandErrors.message(error))),
          dispatcherCallback -> ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcherCallback.accept(dispatcher))
        ));
    }

    @Override
    public void registerAllCommands() {
    }

}
