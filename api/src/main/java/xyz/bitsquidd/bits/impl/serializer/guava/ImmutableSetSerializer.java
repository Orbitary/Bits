/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.guava;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

import java.util.HashSet;
import java.util.Set;

import static xyz.bitsquidd.bits.util.serializer.SerializationManager.SERIALIZER;


@SuppressWarnings({"rawtypes", "unchecked"})
@Serializer
public final class ImmutableSetSerializer extends MultiSerializer<ImmutableSet> {
    private ImmutableSetSerializer() {
        super(ImmutableSet.class);
    }

    @Override
    protected JsonNode serialize(ImmutableSet value) {
        return SERIALIZER.valueToTree(new HashSet<>(value));
    }

    @Override
    public ImmutableSet deserialize(JsonNode node) {
        return ImmutableSet.copyOf(SERIALIZER.convertValue(node, Set.class));
    }

}