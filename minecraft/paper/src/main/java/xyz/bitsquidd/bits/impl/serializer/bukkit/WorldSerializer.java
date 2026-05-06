/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.bukkit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.Bukkit;
import org.bukkit.World;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

import java.util.Optional;


@Serializer
public final class WorldSerializer extends MultiSerializer<World> {
    private WorldSerializer() {
        super(World.class);
    }

    @Override
    protected JsonNode serialize(World value) {
        return TextNode.valueOf(value.getName());
    }

    @Override
    protected World deserialize(JsonNode node) throws JsonParseException {
        String worldName = node.asText();
        return Optional.ofNullable(Bukkit.getWorld(worldName)).orElseThrow(() -> new JsonParseException("World with name '" + worldName + "' not found"));
    }

}
