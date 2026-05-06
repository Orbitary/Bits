/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.impl.serializer.adventure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;

import xyz.bitsquidd.bits.util.serializer.MultiSerializer;
import xyz.bitsquidd.bits.util.serializer.Serializer;


@Serializer
public final class ComponentSerializer extends MultiSerializer<ComponentLike> {
    private ComponentSerializer() {
        super(ComponentLike.class);
    }

    // Although we could consider the JsonComponentSerializer, it is not as efficient as the minimessage format.
    @Override
    protected JsonNode serialize(ComponentLike value) {
        return TextNode.valueOf(MiniMessage.miniMessage().serialize(value.asComponent()));
    }

    @Override
    protected ComponentLike deserialize(JsonNode node) {
        return MiniMessage.miniMessage().deserialize(node.asText());
    }

}