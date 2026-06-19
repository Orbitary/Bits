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

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.paper.wrapper.WorldDefinition;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static xyz.bitsquidd.bits.paper.util.Keys.NSK;


public final class WorldDefinitionArgumentParser extends ArgumentParser<WorldDefinition, Key> {
    private static final Path DIMENSIONS_PATH = Bukkit.getWorldContainer().toPath().resolve("world/dimensions");

    public WorldDefinitionArgumentParser() {
        super(TypeSignature.of(WorldDefinition.class), "WorldDefinition", Key.class);
    }

    @Override
    public WorldDefinition parse(Key data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        return WorldDefinition.of(NSK(data));
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        Set<Key> worldKeys = new HashSet<>();
        try (Stream<Path> namespaces = Files.list(DIMENSIONS_PATH)) {
            namespaces.forEach(namespacePath -> {
                String namespace = namespacePath.getFileName().toString();
                try (Stream<Path> dimensions = Files.list(namespacePath)) {
                    dimensions.forEach(p -> {
                        File dimensionFile = p.toFile();
                        if (!dimensionFile.isDirectory()) return;
                        worldKeys.add(Key.key(namespace, dimensionFile.getName()));
                    });
                } catch (IOException e) {
                    // If we can't read the dimensions directory, we just skip it.
                }
            });
        } catch (IOException e) {
            // If we can't read the dimensions directory, we just return an empty list of suggestions.
            return List::of;
        }

        return () -> worldKeys.stream().map(k -> "\"" + k + "\"").toList();
    }

}
