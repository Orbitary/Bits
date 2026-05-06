/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.joml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joml.Vector3f;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

@Serializer
public final class Vector3fSerializer extends MultiSerializer<Vector3f> {
    private Vector3fSerializer() {
        super(Vector3f.class);
    }

    @Override
    protected JsonNode serialize(Vector3f value) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("x", value.x);
        node.put("y", value.y);
        node.put("z", value.z);
        return node;
    }

    @Override
    protected Vector3f deserialize(JsonNode node) {
        float x = node.get("x").floatValue();
        float y = node.get("y").floatValue();
        float z = node.get("z").floatValue();
        return new Vector3f(x, y, z);
    }

}
