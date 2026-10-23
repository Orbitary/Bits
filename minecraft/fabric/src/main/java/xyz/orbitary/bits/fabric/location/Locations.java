/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.fabric.location;

import net.kyori.adventure.key.Key;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import xyz.orbitary.bits.data.world.Cardinal;
import xyz.orbitary.bits.mc.world.BlockPos;
import xyz.orbitary.bits.mc.world.ChunkPos;
import xyz.orbitary.bits.mc.world.Coord;
import xyz.orbitary.bits.mc.world.Pos;
import xyz.orbitary.bits.mc.world.Rotation;
import xyz.orbitary.bits.mc.world.Vec3;


public final class Locations {
    private Locations() {}


    //region Fabric -> Bits
    public static Vec3 fromFabric(net.minecraft.world.phys.Vec3 vec) {
        return new Vec3(vec.x(), vec.y(), vec.z());
    }

    public static BlockPos fromFabric(net.minecraft.core.BlockPos pos) {
        return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static ChunkPos fromFabric(net.minecraft.world.level.ChunkPos pos) {
        return new ChunkPos(pos.x(), pos.z());
    }

    public static Cardinal fromFabric(Direction direction) {
        return switch (direction) {
            case NORTH -> Cardinal.NORTH;
            case EAST -> Cardinal.EAST;
            case SOUTH -> Cardinal.SOUTH;
            case WEST -> Cardinal.WEST;
            case UP -> Cardinal.UP;
            case DOWN -> Cardinal.DOWN;
        };
    }

    public static Key worldKey(Level level) {
        Identifier id = level.dimension().identifier();
        return Key.key(id.getNamespace(), id.getPath());
    }

    public static Pos toPosition(Entity entity) {
        return new Pos(fromFabric(entity.position()), Rotation.of(entity.getYRot(), entity.getXRot()));
    }

    public static Coord fromFabric(Entity entity) {
        return new Coord.Record(worldKey(entity.level()), toPosition(entity));
    }
    //endregion


    //region Bits -> Fabric
    public static net.minecraft.world.phys.Vec3 toFabric(Vec3 vec) {
        return new net.minecraft.world.phys.Vec3(vec.x(), vec.y(), vec.z());
    }

    public static net.minecraft.core.BlockPos toFabric(BlockPos pos) {
        return new net.minecraft.core.BlockPos(pos.x(), pos.y(), pos.z());
    }

    public static net.minecraft.world.level.ChunkPos toFabric(ChunkPos pos) {
        return new net.minecraft.world.level.ChunkPos(pos.x(), pos.z());
    }

    public static Direction toFabric(Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> Direction.NORTH;
            case EAST -> Direction.EAST;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }
    //endregion


    //region Levels
    public static ServerLevel resolveLevel(MinecraftServer server, Key world) throws IllegalStateException {
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(world.namespace(), world.value()));
        ServerLevel level = server.getLevel(dimension);
        if (level == null) throw new IllegalStateException("Level is not loaded: " + world);
        return level;
    }
    //endregion

}
