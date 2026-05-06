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

import java.time.Duration;


@Serializer
public final class DurationSerializer extends MultiSerializer<Duration> {
    private DurationSerializer() {
        super(Duration.class);
    }

    @Override
    protected JsonNode serialize(Duration value) {
        return LongNode.valueOf(value.toMillis());
    }

    @Override
    protected Duration deserialize(JsonNode node) {
        return Duration.ofMillis(node.asLong());
    }

}