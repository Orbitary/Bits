/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config.node.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.config.ConfigException;
import xyz.bitsquidd.bits.config.node.ConfigNode;
import xyz.bitsquidd.bits.config.node.ConfigSection;
import xyz.bitsquidd.bits.util.serializer.SerializationManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * A {@link ConfigNode} backed by a Jackson {@link JsonNode} scalar (string, number, boolean, array).
 *
 * @since 0.0.14
 */
final class JacksonLeafNode implements ConfigNode {

    private final JsonNode node;
    private final String key;
    private final String path;

    JacksonLeafNode(JsonNode node, String key, String path) {
        this.node = node;
        this.key = key;
        this.path = path;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public boolean isNull() {
        return node.isNull();
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public boolean isList() {
        return node.isArray();
    }

    @Override
    public <T> T require(Class<T> type) throws ConfigException {
        try {
            T value = SerializationManager.SERIALIZER.treeToValue(node, type);
            if (value == null) throw ConfigException.missingValue(path);
            return value;
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            throw ConfigException.typeMismatch(path, type, e);
        }
    }

    @Override
    public <T> Optional<T> get(Class<T> type) {
        try {
            return Optional.ofNullable(SerializationManager.SERIALIZER.treeToValue(node, type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> List<T> getList(Class<T> elementType) {
        if (!node.isArray()) return Collections.emptyList();
        try {
            var type = SerializationManager.SERIALIZER.getTypeFactory()
              .constructCollectionType(List.class, elementType);
            return SerializationManager.SERIALIZER.treeToValue(node, type);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void set(@Nullable Object value) throws ConfigException {
        // Leaf nodes are immutable views- writes go through the parent section
        throw new UnsupportedOperationException(
          "Cannot set a value directly on a leaf node. Use ConfigSection.set(path, value) instead."
        );
    }

    @Override
    public ConfigSection asSection() throws ConfigException {
        throw ConfigException.notASection(path);
    }

}

/**
 * A {@link ConfigNode} representing an absent (missing) path in the tree.
 * <p>
 * All reads return their default values. Writes throw {@link UnsupportedOperationException}
 * because there is no parent node to write into- use {@link ConfigSection#set(String, Object)}.
 *
 * @since 0.0.14
 */
final class AbsentConfigNode implements ConfigNode {

    private final String path;

    private AbsentConfigNode(String path) {
        this.path = path;
    }

    static AbsentConfigNode at(String path) {
        return new AbsentConfigNode(path);
    }

    @Override
    public String key() {
        return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public <T> List<T> getList(Class<T> elementType) {
        return Collections.emptyList();
    }

    @Override
    public <T> Optional<T> get(Class<T> type) {
        return Optional.empty();
    }

    @Override
    public void set(@Nullable Object value) {
        throw new UnsupportedOperationException("Cannot write to an absent node at '" + path + "'. Use ConfigSection.set(path, value) on the parent section.");
    }

    @Override
    public ConfigSection asSection() throws ConfigException {
        throw ConfigException.missingValue(path);
    }

}