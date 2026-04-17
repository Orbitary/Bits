/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.serializer;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import xyz.bitsquidd.bits.util.reflection.ReflectionException;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;
import xyz.bitsquidd.bits.util.reflection.ScannerFlags;
import xyz.bitsquidd.bits.util.serializer.jackson.NullableAwareIntrospector;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class SerializationManager {
    private SerializationManager() {}

    public static final ObjectMapper SERIALIZER = createMapper();

    private static ObjectMapper createMapper() {
        JsonMapper.Builder builder = JsonMapper.builder()
          .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
          .disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
          .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
          .annotationIntrospector(new NullableAwareIntrospector());

        getSerializers().forEach(serializer -> registerSerializer(serializer, builder));
        return builder.build();
    }

    private static <T> void registerSerializer(MultiSerializer<T> serializer, JsonMapper.Builder builder) {
        SimpleModule module = new SimpleModule();
        Class<T> targetClass = serializer.getTargetClass();
        module.addSerializer(targetClass, serializer.jacksonSerializer());
        module.addDeserializer(targetClass, serializer.jacksonDeserializer());
        builder.addModule(module);
    }

    @SuppressWarnings("unchecked")
    private static Set<MultiSerializer<?>> getSerializers() {
        return (Set<MultiSerializer<?>>)ReflectionUtils.Scanner.tryGetAnnotatedClasses("*", Serializer.class, ScannerFlags.DEFAULT)
          .stream()
          .filter(MultiSerializer.class::isAssignableFrom)
          .map(clazz -> {
              try {
                  return ReflectionUtils.Instance.create(clazz);
              } catch (ReflectionException e) {
                  return null;
              }
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
    }

    /**
     * The Jackson serializer is great, here's a couple useful methods!
     * String json       = SERIALIZER.writeValueAsString(obj)
     * Object obj        = SERIALIZER.readValue(jsonString, Object.class)
     * JsonNode json     = SERIALIZER.valueToTree(obj);
     * Object obj        = SERIALIZER.treeToValue(jsonNode, Object.class);
     * <p>
     * JsonNode json     = SERIALIZER.readTree(jsonString);    (from String)
     * JsonNode json     = SERIALIZER.readTree(file);          (from File)
     * <p>
     * List<MyType> list = SERIALIZER.readValue(jsonString, new TypeReference<List<MyType>>() {});
     */

}
