/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location;

import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import xyz.orbitary.bits.data.world.Cardinal;
import xyz.orbitary.bits.mc.world.BlockPos;
import xyz.orbitary.bits.mc.world.ChunkPos;
import xyz.orbitary.bits.mc.world.Location;
import xyz.orbitary.bits.mc.world.Position;
import xyz.orbitary.bits.mc.world.Rotation;
import xyz.orbitary.bits.mc.world.Vec3;


public final class PaperLocations {
    private PaperLocations() {}


    //region Bukkit -> Bits
    public static Vec3 fromBukkit(Vector vector) {
        return new Vec3(vector.getX(), vector.getY(), vector.getZ());
    }

    public static BlockPos fromBukkit(Block block) {
        return new BlockPos(block.getX(), block.getY(), block.getZ());
    }

    public static ChunkPos fromBukkit(Chunk chunk) {
        return new ChunkPos(chunk.getX(), chunk.getZ(), chunk.getWorld().key());
    }

    public static Location fromBukkit(org.bukkit.Location location) throws IllegalArgumentException {
        return new Location(requireWorld(location).key(), toPosition(location));
    }

    public static Position toPosition(org.bukkit.Location location) {
        return new Position(
          new Vec3(location.getX(), location.getY(), location.getZ()),
          Rotation.of(location.getYaw(), location.getPitch())
        );
    }

    public static Location fromEntity(Entity entity) {
        return fromBukkit(entity.getLocation());
    }

    /**
     * @return the block containing {@code location} (floored, not rounded).
     */
    public static BlockPos toBlockPos(org.bukkit.Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Rotation rotationOf(org.bukkit.Location location) {
        return Rotation.of(location.getYaw(), location.getPitch());
    }

    public static Rotation rotationOf(Entity entity) {
        return rotationOf(entity.getLocation());
    }

    public static Cardinal fromBukkit(BlockFace face) throws IllegalArgumentException {
        return switch (face) {
            case NORTH -> Cardinal.NORTH;
            case EAST -> Cardinal.EAST;
            case SOUTH -> Cardinal.SOUTH;
            case WEST -> Cardinal.WEST;
            case UP -> Cardinal.UP;
            case DOWN -> Cardinal.DOWN;
            default -> throw new IllegalArgumentException("Not a cardinal direction: " + face);
        };
    }
    //endregion


    //region Bits -> Bukkit
    public static Vector toBukkit(Vec3 vec) {
        return new Vector(vec.x(), vec.y(), vec.z());
    }

    public static Block toBlock(World world, BlockPos pos) {
        return world.getBlockAt(pos.x(), pos.y(), pos.z());
    }

    /**
     * @return the centre of the block, facing {@link Rotation#ZERO}.
     */
    public static org.bukkit.Location toBukkit(BlockPos pos, World world) {
        return new org.bukkit.Location(world, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5);
    }

    /**
     * Sets the rotation of {@code location} in place and returns it.
     */
    public static org.bukkit.Location applyRotation(org.bukkit.Location location, Rotation rotation) {
        location.setRotation(rotation.yaw(), rotation.pitch());
        return location;
    }

    /**
     * Resets the rotation of {@code location} in place and returns it.
     */
    public static org.bukkit.Location clearRotation(org.bukkit.Location location) {
        return applyRotation(location, Rotation.ZERO);
    }

    public static org.bukkit.Location toBukkit(Position position, World world) {
        Vec3 pos = position.pos();
        Rotation rot = position.rot();
        return new org.bukkit.Location(world, pos.x(), pos.y(), pos.z(), rot.yaw(), rot.pitch());
    }

    public static org.bukkit.Location toBukkit(Location location, World world) throws IllegalArgumentException {
        if (!world.key().equals(location.world())) throw new IllegalArgumentException("Location is in " + location.world() + " but world is " + world.key());
        return toBukkit(location.position(), world);
    }

    public static org.bukkit.Location toBukkit(Location location) throws IllegalArgumentException {
        return toBukkit(location.position(), resolveWorld(location.world()));
    }

    public static BlockFace toBukkit(Cardinal cardinal) {
        return switch (cardinal) {
            case NORTH -> BlockFace.NORTH;
            case EAST -> BlockFace.EAST;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
            case UP -> BlockFace.UP;
            case DOWN -> BlockFace.DOWN;
        };
    }
    //endregion


    //region Worlds
    public static World resolveWorld(Key key) throws IllegalStateException {
        World world = Bukkit.getWorld(new NamespacedKey(key.namespace(), key.value()));
        if (world == null) throw new IllegalStateException("World is not loaded: " + key);
        return world;
    }

    private static World requireWorld(org.bukkit.Location location) {
        World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("Location has no world");
        return world;
    }
    //endregion

}
