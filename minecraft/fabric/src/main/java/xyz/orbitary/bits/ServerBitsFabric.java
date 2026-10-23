/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */
package xyz.orbitary.bits;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xyz.orbitary.bits.fabric.module.command.FabricCommandManager;
import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.log.FabricBitsLogger;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.wrapper.collection.AddableList;
import xyz.orbitary.bits.mc.module.command.CommandErrors;
import xyz.orbitary.bits.mc.permission.Permission;

import java.util.Locale;

@Environment(EnvType.SERVER)
public class ServerBitsFabric extends BitsFabric {

    private @Nullable MinecraftServer server;
    private @Nullable MinecraftServerAudiences adventure;

    protected ServerBitsFabric() {}

    public static ServerBitsFabric get() {
        return (ServerBitsFabric) Bits.get();
    }

    public static ServerBitsFabric generic() {
        new FabricBitsLogger(org.slf4j.LoggerFactory.getLogger("BitsServer"), Logger.LogFlags.defaultFlags());
        return new ServerBitsFabric();
    }

    @Override
    public void startup() {
        super.startup();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            this.adventure = MinecraftServerAudiences.of(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            this.server = null;
            this.adventure = null;
        });
        ServerTickEvents.END_SERVER_TICK.register(_ -> tickAll());
    }

    @Override
    public Audience getAll() {
        if (server == null || adventure == null) return Audience.empty();
        return adventure.audience(server.getPlayerList().getPlayers());
    }

    @Override
    public Locale getLocale(Audience audience) {
        if (audience instanceof ServerPlayer player) return Locale.forLanguageTag(player.clientInformation().language());
        return Locale.getDefault();
    }

    @Override
    public void registerPermission(Permission permission) {
        // Permissions are checked against the permissions API, so there is nothing to register.
    }

    @Override
    public boolean hasPermission(Audience audience, Permission permission) {
        if (audience instanceof ServerPlayer player) return Permissions.check(player, permission.toString());
        return true;
    }

    @Override
    protected AddableList<BitsModule> modules() {
        return super.modules().add(new FabricCommandManager<CommandSourceStack>(
          arguments -> {},
          (source, error) -> source.sendFailure(Component.literal(CommandErrors.message(error))),
          (source, node) -> Permissions.check(source, node, false),
          dispatcherCallback -> CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcherCallback.accept(dispatcher))
        ));
    }

    @Override
    public void registerAllCommands() {
    }

}
