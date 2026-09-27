/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.impl.serializer.joml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joml.Vector2i;

import xyz.orbitary.bits.util.serializer.MultiSerializer;
import xyz.orbitary.bits.util.serializer.Serializer;


@Serializer
public final class Vector2iSerializer extends MultiSerializer<Vector2i> {
    private Vector2iSerializer() {
        super(Vector2i.class);
    }

    @Override
    protected JsonNode serialize(Vector2i value) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("x", value.x);
        node.put("y", value.y);
        return node;
    }

    @Override
    protected Vector2i deserialize(JsonNode node) {
        int x = node.get("x").asInt();
        int y = node.get("y").asInt();
        return new Vector2i(x, y);
    }

}
