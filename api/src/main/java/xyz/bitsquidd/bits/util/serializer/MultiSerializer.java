/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class MultiSerializer<T> {
    private final Class<T> clazz;

    protected MultiSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    public final Class<T> getTargetClass() {
        return clazz;
    }


    protected abstract JsonNode serialize(T value) throws JacksonException;

    public final StdSerializer<? super T> jacksonSerializer() {
        return new StdSerializer<>(clazz) {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider ctx) throws IOException {
                JsonNode node = MultiSerializer.this.serialize(value);
                gen.writeTree(node);
            }
        };
    }


    protected abstract T deserialize(JsonNode node) throws JacksonException;

    public final StdDeserializer<? extends T> jacksonDeserializer() {
        return new StdDeserializer<>(clazz) {
            @Override
            public T deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
                JsonNode node = ctx.readTree(parser);
                return MultiSerializer.this.deserialize(node);
            }
        };
    }


    public @Nullable JsonSerializer<? super T> jacksonKeySerializer() {
        return null;
    }

    public @Nullable KeyDeserializer jacksonKeyDeserializer() {
        return null;
    }

}