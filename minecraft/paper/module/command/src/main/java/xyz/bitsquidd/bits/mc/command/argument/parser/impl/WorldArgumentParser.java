/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;


public final class WorldArgumentParser extends ArgumentParser<World, Key> {

    public WorldArgumentParser() {
        super(TypeSignature.of(World.class), "World", Key.class);
    }

    @Override
    public World parse(Key data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        World world = Bukkit.getWorld(data);
        if (world == null) throw ExceptionBuilder.createCommandException("World not found: " + data + ".");
        return world;
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        return _ -> Bukkit.getWorlds().stream().map(w -> "\"" + w.key() + "\"").toList();
    }

}
