/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.module.command;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import xyz.orbitary.bits.mc.module.command.BrigadierArgument;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;

import java.util.UUID;


/**
 * The argument types Paper commands accept out of the box. Where Minecraft has its own Brigadier argument, that is
 * used, so suggestions and errors match vanilla.
 *
 * @since 0.1.6
 */
final class PaperArguments {

    private PaperArguments() {}

    static void registerDefaults(BrigadierArguments<CommandSourceStack> arguments) {
        CommandBuildContext buildContext = CommandBuildContext.simple(MinecraftServer.getServer().registryAccess(), FeatureFlags.DEFAULT_FLAGS);
        arguments.register(
          EntitySelector.class,
          BrigadierArgument.of(
            EntityArgument.entities(),
            (context, name) -> context.getArgument(name, EntitySelector.class)
          )
        );

        arguments.register(
          Player.class,
          BrigadierArgument.of(
            EntityArgument.player(),
            (context, name) -> context.getArgument(name, EntitySelector.class)
              .findSinglePlayer(context.getSource())
              .getBukkitEntity()
              .getPlayer()
          )
        );
        arguments.registerCollection(
          Player.class,
          BrigadierArgument.of(
            EntityArgument.players(),
            (context, name) -> context.getArgument(name, EntitySelector.class)
              .findPlayers(context.getSource())
              .stream()
              .map(player -> player.getBukkitEntity().getPlayer())
              .toList()
          )
        );

        arguments.register(
          Entity.class,
          BrigadierArgument.of(
            EntityArgument.entity(),
            (context, name) -> context.getArgument(name, EntitySelector.class)
              .findSingleEntity(context.getSource())
              .getBukkitEntity()
          )
        );
        arguments.registerCollection(
          Entity.class,
          BrigadierArgument.of(
            EntityArgument.entities(),
            (context, name) -> context.getArgument(name, EntitySelector.class)
              .findEntities(context.getSource())
              .stream()
              .map(net.minecraft.world.entity.Entity::getBukkitEntity)
              .toList()
          )
        );

        arguments.register(
          Location.class,
          BrigadierArgument.of(
            Vec3Argument.vec3(), (context, name) -> {
                CommandSourceStack source = context.getSource();
                var position = context.getArgument(name, Coordinates.class).getPosition(source);
                return new Location(source.getBukkitWorld(), position.x, position.y, position.z);
            }
          )
        );
        arguments.register(
          BlockPos.class,
          BrigadierArgument.of(
            BlockPosArgument.blockPos(),
            (context, name) -> context.getArgument(name, Coordinates.class).getBlockPos(context.getSource())
          )
        );
        arguments.register(
          World.class,
          BrigadierArgument.of(
            DimensionArgument.dimension(),
            (context, name) -> DimensionArgument.getDimension(context, name).getWorld()
          )
        );
        arguments.register(
          Key.class,
          BrigadierArgument.of(
            IdentifierArgument.id(), (context, name) -> {
                Identifier id = context.getArgument(name, Identifier.class);
                return Key.key(id.getNamespace(), id.getPath());
            }
          )
        );

        arguments.register(UUID.class, BrigadierArgument.of(UuidArgument.uuid(), UuidArgument::getUuid));
        arguments.register(GameMode.class, BrigadierArgument.of(GameModeArgument.gameMode(),
          (context, name) -> GameMode.valueOf(GameModeArgument.getGameMode(context, name).name())));
        arguments.register(Component.class, BrigadierArgument.of(ComponentArgument.textComponent(buildContext),
          (context, name) -> PaperAdventure.asAdventure(ComponentArgument.getResolvedComponent(context, name))));
        arguments.register(Particle.class, BrigadierArgument.of(ParticleArgument.particle(buildContext),
          (context, name) -> CraftParticle.minecraftToBukkit(ParticleArgument.getParticle(context, name).getType())));
    }


}
