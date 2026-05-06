/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.time;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;

import java.time.Instant;


@Serializer
public final class InstantSerializer extends MultiSerializer<Instant> {
    private InstantSerializer() {
        super(Instant.class);
    }

    @Override
    protected JsonNode serialize(Instant value) {
        return LongNode.valueOf(value.toEpochMilli());
    }

    @Override
    protected Instant deserialize(JsonNode node) {
        return Instant.ofEpochMilli(node.asLong());
    }

}