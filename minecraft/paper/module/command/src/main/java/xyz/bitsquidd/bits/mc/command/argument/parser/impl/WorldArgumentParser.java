/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.World;

import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;
import java.util.function.Supplier;

public final class WorldArgumentParser extends AbstractArgumentParser<World> {

    public WorldArgumentParser() {
        super(TypeSignature.of(World.class), "World");
    }

    @Override
    public World parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        String inputString = singletonInputValidation(inputObjects, String.class);

        World world = Bukkit.getWorld(inputString);
        if (world == null) throw ExceptionBuilder.createCommandException("World not found: " + inputString + ".");
        return world;
    }

    @Override
    public Supplier<List<String>> getSuggestions() {
        return () -> Bukkit.getWorlds().stream().map(World::getName).toList();
    }

}
