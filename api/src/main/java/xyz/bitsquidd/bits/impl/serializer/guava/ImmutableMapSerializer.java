/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.guava;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

import java.util.HashMap;
import java.util.Map;

import static xyz.bitsquidd.bits.util.serializer.SerializationManager.SERIALIZER;


@SuppressWarnings({"rawtypes", "unchecked"})
@Serializer
public final class ImmutableMapSerializer extends MultiSerializer<ImmutableMap> {
    private ImmutableMapSerializer() {
        super(ImmutableMap.class);
    }

    @Override
    protected JsonNode serialize(ImmutableMap value) {
        return SERIALIZER.valueToTree(new HashMap<>(value));
    }

    @Override
    public ImmutableMap deserialize(JsonNode node) {
        Map<?, ?> map = SERIALIZER.convertValue(node, Map.class);
        return ImmutableMap.copyOf(map);
    }

}