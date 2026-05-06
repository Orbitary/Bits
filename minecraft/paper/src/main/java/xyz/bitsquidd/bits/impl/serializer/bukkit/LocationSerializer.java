/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.bukkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Location;
import org.bukkit.World;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

import static xyz.bitsquidd.bits.util.serializer.SerializationManager.SERIALIZER;


@Serializer
public final class LocationSerializer extends MultiSerializer<Location> {
    private LocationSerializer() {
        super(Location.class);
    }

    @Override
    protected JsonNode serialize(Location value) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.set("world", SERIALIZER.valueToTree(value.getWorld()));
        node.put("x", value.getX());
        node.put("y", value.getY());
        node.put("z", value.getZ());
        node.put("yaw", value.getYaw());
        node.put("pitch", value.getPitch());
        return node;
    }

    @Override
    protected Location deserialize(JsonNode node) throws JsonProcessingException {
        World world = SERIALIZER.treeToValue(node.get("world"), World.class);
        double x = node.get("x").asDouble();
        double y = node.get("y").asDouble();
        double z = node.get("z").asDouble();
        float yaw = node.get("yaw").floatValue();
        float pitch = node.get("pitch").floatValue();

        return new Location(world, x, y, z, yaw, pitch);
    }

}
