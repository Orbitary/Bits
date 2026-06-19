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
import org.bukkit.NamespacedKey;

import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
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
import java.util.function.Supplier;
import java.util.stream.Stream;


public final class WorldDefinitionArgumentParser extends AbstractArgumentParser<WorldDefinition> {
    private static final Path DIMENSIONS_PATH = Bukkit.getWorldContainer().toPath().resolve("world/dimensions");

    public WorldDefinitionArgumentParser() {
        super(TypeSignature.of(WorldDefinition.class), "WorldDefinition");
    }

    @Override
    public WorldDefinition parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        String inputString = singletonInputValidation(inputObjects, String.class);

        NamespacedKey key;
        try {
            key = NamespacedKey.fromString(inputString);
            if (key == null) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            throw ExceptionBuilder.createCommandException("Invalid world key: " + inputString + ".");
        }

        WorldDefinition worldDefinition = WorldDefinition.of(key);
        return worldDefinition;
    }

    @Override
    public Supplier<List<String>> getSuggestions() {
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
