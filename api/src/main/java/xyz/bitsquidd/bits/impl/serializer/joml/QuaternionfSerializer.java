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
import org.joml.Quaternionf;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;


@Serializer
public final class QuaternionfSerializer extends MultiSerializer<Quaternionf> {
    private QuaternionfSerializer() {
        super(Quaternionf.class);
    }

    @Override
    protected JsonNode serialize(Quaternionf value) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("x", value.x);
        node.put("y", value.y);
        node.put("z", value.z);
        node.put("w", value.w);
        return node;
    }

    @Override
    protected Quaternionf deserialize(JsonNode node) {
        float x = node.get("x").floatValue();
        float y = node.get("y").floatValue();
        float z = node.get("z").floatValue();
        float w = node.get("w").floatValue();
        return new Quaternionf(x, y, z, w);
    }

}
