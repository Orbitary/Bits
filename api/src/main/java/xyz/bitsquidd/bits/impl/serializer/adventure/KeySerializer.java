/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.adventure;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import net.kyori.adventure.key.Key;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

@Serializer
public final class KeySerializer extends MultiSerializer<Key> {
    private KeySerializer() {
        super(Key.class);
    }

    @Override
    protected JsonNode serialize(Key value) {
        return TextNode.valueOf(value.asString());
    }

    @Override
    protected Key deserialize(JsonNode node) {
        return Key.key(node.asText());
    }


    @Override
    public JsonSerializer<? super Key> jacksonKeySerializer() {
        return new StdKeySerializers.StringKeySerializer();
    }

    @Override
    public KeyDeserializer jacksonKeyDeserializer() {
        return new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) {
                return Key.key(key);
            }
        };
    }

}