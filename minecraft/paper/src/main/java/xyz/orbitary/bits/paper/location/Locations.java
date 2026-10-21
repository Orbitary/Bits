/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location;

import net.kyori.adventure.key.Key;
import net.minecraft.core.Vec3i;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.data.world.Cardinal;
import xyz.orbitary.bits.mc.world.ChunkPos;
import xyz.orbitary.bits.mc.world.Position;
import xyz.orbitary.bits.mc.world.Rotation;
import xyz.orbitary.bits.mc.world.Vec3;
import xyz.orbitary.bits.paper.location.wrapper.BlockPos;
import xyz.orbitary.bits.paper.location.wrapper.Locatable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;


/**
 * Suite of location-related utility methods.
 *
 * @since 0.0.11
 */
public final class Locations {
    private Locations() {}


    //region Utils
    public static boolean isSameWorld(Location... locations) {
        if (locations.length == 0) return false;
        World world = locations[0].getWorld();

        for (Location location : locations) {
            if (!location.getWorld().equals(world)) return false;
        }
        return true;
    }

    //endregion


    //region Distance

    /**
     * Safe distance check between two locations.
     * Returns false if either location is null or they are in different worlds.
     *
     * @since 0.0.11
     */
    public static boolean isWithinDistance(@Nullable Location loc1, @Nullable Location loc2, double distance) {
        return isWithinDistanceSq(loc1, loc2, distance * distance);
    }

    public static boolean isWithinDistanceSq(@Nullable Location loc1, @Nullable Location loc2, double distanceSq) {
        if (loc1 == null || loc2 == null) return false;
        return getDistanceSq(loc1, loc2) <= distanceSq;
    }

    public static double getDistance(Location location1, Location location2) {
        if (!isSameWorld(location1, location2)) return Double.MAX_VALUE;
        return Math.sqrt(getDistanceSq(location1, location2));
    }

    public static double getDistanceSq(Location location1, Location location2) {
        if (!isSameWorld(location1, location2)) return Double.MAX_VALUE;

        double dx = location1.getX() - location2.getX();
        double dy = location1.getY() - location2.getY();
        double dz = location1.getZ() - location2.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    public static double getHorizontalDistance(Location location1, Location location2) {
        return Math.sqrt(getHorizontalDistanceSq(location1, location2));
    }

    public static double getHorizontalDistanceSq(Location location1, Location location2) {
        if (!isSameWorld(location1, location2)) return Double.MAX_VALUE;

        double dx = location1.getX() - location2.getX();
        double dz = location1.getZ() - location2.getZ();
        return dx * dx + dz * dz;
    }
    //endregion


    //region Collection Operations

    /**
     * Calculates the midpoint BlockPos coordinates from a collection of {@link Locatable}s.
     *
     * @since 0.0.13
     * @deprecated Use {@link xyz.orbitary.bits.mc.world.Vec3#midpoint(Collection)}.
     */
    @Deprecated(forRemoval = true, since = "0.1.1")
    public static BlockPos getMidpoint(Collection<? extends Locatable> locatables) {
        if (locatables.isEmpty()) throw new IllegalArgumentException("Locatables cannot be empty");

        int amount = locatables.size();
        double sumX = locatables.stream().mapToDouble(l -> l.asVector().getX()).sum();
        double sumY = locatables.stream().mapToDouble(l -> l.asVector().getY()).sum();
        double sumZ = locatables.stream().mapToDouble(l -> l.asVector().getZ()).sum();

        return BlockPos.of(sumX / amount, sumY / amount, sumZ / amount);
    }

    /**
     * Calculates the minimum BlockPos coordinates from a collection of {@link Locatable}s.
     *
     * @since 0.0.13
     * @deprecated Use {@link xyz.orbitary.bits.mc.world.Vec3#min(Collection)}.
     */
    @Deprecated(forRemoval = true, since = "0.1.1")
    public static BlockPos getMinLocation(Collection<? extends Locatable> locatables) {
        if (locatables.isEmpty()) throw new IllegalArgumentException("Locatables cannot be empty");

        return BlockPos.of(
          locatables.stream().mapToDouble(l -> l.asVector().getX()).min().orElseThrow(() -> new IllegalArgumentException("Error computing min x")),
          locatables.stream().mapToDouble(l -> l.asVector().getY()).min().orElseThrow(() -> new IllegalArgumentException("Error computing min y")),
          locatables.stream().mapToDouble(l -> l.asVector().getZ()).min().orElseThrow(() -> new IllegalArgumentException("Error computing min z"))
        );
    }

    /**
     * Calculates the maximum BlockPos coordinates from a collection of {@link Locatable}s.
     *
     * @since 0.0.13
     * @deprecated Use {@link xyz.orbitary.bits.mc.world.Vec3#max(Collection)}.
     */
    @Deprecated(forRemoval = true, since = "0.1.1")
    public static BlockPos getMaxLocation(Collection<? extends Locatable> locatables) {
        if (locatables.isEmpty()) throw new IllegalArgumentException("Locatables cannot be empty");

        return BlockPos.of(
          locatables.stream().mapToDouble(l -> l.asVector().getX()).max().orElseThrow(() -> new IllegalArgumentException("Error computing max x")),
          locatables.stream().mapToDouble(l -> l.asVector().getY()).max().orElseThrow(() -> new IllegalArgumentException("Error computing max y")),
          locatables.stream().mapToDouble(l -> l.asVector().getZ()).max().orElseThrow(() -> new IllegalArgumentException("Error computing max z"))
        );
    }
    //endregion


    //region Bukkit -> Bits
    public static Vec3 fromBukkit(Vector vector) {
        return new Vec3(vector.getX(), vector.getY(), vector.getZ());
    }

    public static xyz.orbitary.bits.mc.world.BlockPos fromBukkit(Block block) {
        return new xyz.orbitary.bits.mc.world.BlockPos(block.getX(), block.getY(), block.getZ());
    }

    public static ChunkPos fromBukkit(Chunk chunk) {
        return new ChunkPos(chunk.getX(), chunk.getZ(), chunk.getWorld().key());
    }

    public static xyz.orbitary.bits.mc.world.Location fromBukkit(Location location) throws IllegalArgumentException {
        return new xyz.orbitary.bits.mc.world.Location(requireWorld(location).key(), toPosition(location));
    }

    public static xyz.orbitary.bits.mc.world.Location fromBukkit(Entity entity) {
        return fromBukkit(entity.getLocation());
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

    public static Position toPosition(Location location) {
        return new Position(
          new Vec3(location.getX(), location.getY(), location.getZ()),
          Rotation.of(location.getYaw(), location.getPitch())
        );
    }

    public static Position toPosition(Entity entity) {
        return toPosition(entity.getLocation());
    }

    public static xyz.orbitary.bits.mc.world.BlockPos toBlockPos(Location location) {
        return new xyz.orbitary.bits.mc.world.BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Rotation rotationOf(Location location) {
        return Rotation.of(location.getYaw(), location.getPitch());
    }

    public static Rotation rotationOf(Entity entity) {
        return rotationOf(entity.getLocation());
    }
    //endregion


    //region Bits -> Bukkit
    public static Vector toBukkit(Vec3 vec) {
        return new Vector(vec.x(), vec.y(), vec.z());
    }

    public static net.minecraft.world.phys.Vec3 toNMS(Vec3 vec) {
        return new net.minecraft.world.phys.Vec3(vec.x(), vec.y(), vec.z());
    }

    public static Block toBlock(World world, xyz.orbitary.bits.mc.world.BlockPos pos) {
        return world.getBlockAt(pos.x(), pos.y(), pos.z());
    }

    public static Location toBukkit(xyz.orbitary.bits.mc.world.BlockPos pos, World world) {
        return new Location(world, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5);
    }

    public static Vec3i toNMS(xyz.orbitary.bits.mc.world.BlockPos pos) {
        return new Vec3i(pos.x(), pos.y(), pos.z());
    }

    public static Location toBukkit(Position position, World world) {
        Vec3 pos = position.vec();
        Rotation rot = position.rot();
        return new Location(world, pos.x(), pos.y(), pos.z(), rot.yaw(), rot.pitch());
    }

    /**
     * @return the location at {@code vec} facing {@link Rotation#ZERO}.
     */
    public static Location toBukkit(Vec3 vec, World world) {
        return new Location(world, vec.x(), vec.y(), vec.z());
    }

    public static Location toBukkit(xyz.orbitary.bits.mc.world.Location location, World world) throws IllegalArgumentException {
        if (!world.key().equals(location.world())) throw new IllegalArgumentException("Location is in " + location.world() + " but world is " + world.key());
        return toBukkit(location.pos(), world);
    }

    public static Location toBukkit(xyz.orbitary.bits.mc.world.Location location) throws IllegalStateException {
        return toBukkit(location.pos(), resolveWorld(location.world()));
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

    public static Location applyRotation(Location location, Rotation rotation) {
        location.setRotation(rotation.yaw(), rotation.pitch());
        return location;
    }

    public static Location clearRotation(Location location) {
        return applyRotation(location, Rotation.ZERO);
    }
    //endregion


    //region Vertical Search

    /**
     * Searches upwards from {@code location} for the first block that passes {@code filter} and has a collision shape.
     *
     * @return the location on the bottom of that block's bounding box, or empty if none is found.
     * @since 0.1.5
     */
    public static Optional<Location> getCeilingLocation(Location location, Predicate<Block> filter) {
        Location temp = location.clone();
        temp.setY(Math.max(temp.getY(), location.getWorld().getMinHeight()));
        clearRotation(temp);

        while (temp.getY() <= location.getWorld().getMaxHeight()) {
            if (isValidForVerticalSearch(temp.getBlock(), filter)) {
                return Optional.of(new Location(temp.getWorld(), temp.getX(), temp.getBlock().getBoundingBox().getMinY(), temp.getZ(), temp.getYaw(), temp.getPitch()));
            }
            temp.add(0, 1, 0);
        }

        return Optional.empty();
    }

    /**
     * Searches downwards from {@code location} for the first block that passes {@code filter} and has a collision shape.
     *
     * @return the location on the top of that block's bounding box, or empty if none is found.
     * @since 0.1.5
     */
    public static Optional<Location> getGroundLocation(Location location, Predicate<Block> filter) {
        Location temp = location.clone();
        temp.setY(Math.min(temp.getY(), location.getWorld().getMaxHeight()));
        clearRotation(temp);

        while (temp.getY() >= location.getWorld().getMinHeight()) {
            if (isValidForVerticalSearch(temp.getBlock(), filter)) {
                return Optional.of(new Location(temp.getWorld(), temp.getX(), temp.getBlock().getBoundingBox().getMaxY(), temp.getZ(), temp.getYaw(), temp.getPitch()));
            }
            temp.add(0, -1, 0);
        }

        return Optional.empty();
    }

    private static boolean isValidForVerticalSearch(Block block, Predicate<Block> filter) {
        return filter.test(block) && !block.getCollisionShape().getBoundingBoxes().isEmpty();
    }
    //endregion


    //region Worlds
    public static World resolveWorld(Key key) throws IllegalStateException {
        World world = Bukkit.getWorld(new NamespacedKey(key.namespace(), key.value()));
        if (world == null) throw new IllegalStateException("World is not loaded: " + key);
        return world;
    }

    private static World requireWorld(Location location) {
        World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("Location has no world");
        return world;
    }
    //endregion

}