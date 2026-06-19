/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import org.bukkit.Location;
import org.bukkit.World;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;


public final class LocationArgumentParser extends ArgumentParser<Location, LocationArgumentParser.LocationData> {

    public record LocationData(
      double x,
      double y,
      double z,
      World world
    ) {}

    public LocationArgumentParser() {
        super(TypeSignature.of(Location.class), "Location", LocationData.class);
    }


    @Override
    public Location parse(LocationData data, BitsCommandContext<?> ctx) {
        return new Location(data.world(), data.x(), data.y(), data.z());
    }

}
