/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.bukkit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.util.Vector;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;


@Serializer
public final class VectorSerializer extends MultiSerializer<Vector> {
    private VectorSerializer() {
        super(Vector.class);
    }

    @Override
    protected JsonNode serialize(Vector value) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("x", value.getX());
        node.put("y", value.getY());
        node.put("z", value.getZ());
        return node;
    }

    @Override
    protected Vector deserialize(JsonNode node) {
        float x = node.get("x").floatValue();
        float y = node.get("y").floatValue();
        float z = node.get("z").floatValue();
        return new Vector(x, y, z);
    }

}
