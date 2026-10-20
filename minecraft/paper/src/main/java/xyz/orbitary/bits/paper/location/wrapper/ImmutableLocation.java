/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.paper.location.wrapper;

import org.bukkit.Location;
import org.bukkit.World;


/**
 * Immutable representation of a Bukkit {@link Location}.
 *
 * @since 0.0.11
 * @deprecated Use {@link xyz.orbitary.bits.mc.world.Location}, converted with {@link xyz.orbitary.bits.paper.location.Locations}.
 */
@Deprecated(forRemoval = true, since = "0.1.1")
public record ImmutableLocation(
  World world,
  double x,
  double y,
  double z,
  float yaw,
  float pitch
) {
    public static ImmutableLocation of(Location loc) {
        return new ImmutableLocation(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public Location toBukkit() {
        return new Location(world, x, y, z, yaw, pitch);
    }

}