/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.guava;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

import static xyz.bitsquidd.bits.util.serializer.SerializationManager.SERIALIZER;


@SuppressWarnings({"rawtypes", "unchecked"})
@Serializer
public final class ImmutableListSerializer extends MultiSerializer<ImmutableList> {
    private ImmutableListSerializer() {
        super(ImmutableList.class);
    }

    @Override
    protected JsonNode serialize(ImmutableList value) {
        return SERIALIZER.valueToTree(new ArrayList<>(value));
    }

    @Override
    public ImmutableList deserialize(JsonNode node) {
        return ImmutableList.copyOf(SERIALIZER.convertValue(node, List.class));
    }

}