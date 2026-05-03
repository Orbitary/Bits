/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.configuration.node.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.configuration.ConfigException;
import xyz.bitsquidd.bits.configuration.node.ConfigNode;
import xyz.bitsquidd.bits.configuration.node.ConfigSection;
import xyz.bitsquidd.bits.util.serializer.SerializationManager;

import java.util.*;

/**
 * Jackson {@link ObjectNode}-backed implementation of {@link ConfigSection}.
 * <p>
 * Used by both {@code YamlConfigLoader} (which parses YAML into an ObjectNode)
 * and {@code JsonConfigLoader} (which parses JSON natively).
 * <p>
 * All typed reads/writes go through {@code SerializationManager.SERIALIZER},
 * meaning every registered {@code MultiSerializer} is automatically available.
 *
 * @since 0.0.14
 */
public final class JacksonConfigSection implements ConfigSection {

    static final String VERSION_KEY = "config-version";

    private final ObjectNode node;
    private final String key;
    private final String path;

    public JacksonConfigSection(ObjectNode node, String key, String path) {
        this.node = node;
        this.key = key;
        this.path = path;
    }

    /**
     * Creates a root section (empty path).
     */
    public static JacksonConfigSection root(ObjectNode node) {
        return new JacksonConfigSection(node, "", "");
    }

    /**
     * Returns the underlying {@link ObjectNode} for use by loaders.
     */
    public ObjectNode objectNode() {
        return node;
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
        return false;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    @Override
    public boolean isList() {
        return false;
    }


    @Override
    public ConfigNode node(String dotPath) {
        String[] parts = dotPath.split("\\.", 2);
        JsonNode child = node.get(parts[0]);

        if (child == null || child.isNull()) {
            return AbsentConfigNode.at(childPath(parts[0]));
        }

        if (parts.length == 1) {
            // Leaf
            if (child.isObject()) {
                return new JacksonConfigSection((ObjectNode)child, parts[0], childPath(parts[0]));
            }
            return new JacksonLeafNode(child, parts[0], childPath(parts[0]));
        }

        // Recurse into child section
        if (!child.isObject()) return AbsentConfigNode.at(childPath(dotPath));
        return new JacksonConfigSection((ObjectNode)child, parts[0], childPath(parts[0]))
          .node(parts[1]);
    }

    @Override
    public ConfigSection section(String dotPath) throws ConfigException {
        ConfigNode found = node(dotPath);
        if (found.isNull()) throw ConfigException.missingValue(dotPath);
        if (!found.isSection()) throw ConfigException.notASection(dotPath);
        return found.asSection();
    }

    @Override
    public ConfigSection getOrCreateSection(String dotPath) {
        String[] parts = dotPath.split("\\.", 2);
        JsonNode child = node.get(parts[0]);

        if (child == null || !child.isObject()) {
            ObjectNode newChild = SerializationManager.SERIALIZER.createObjectNode();
            node.set(parts[0], newChild);
            child = newChild;
        }

        JacksonConfigSection childSection = new JacksonConfigSection(
          (ObjectNode)child, parts[0], childPath(parts[0])
        );

        return (parts.length == 1) ? childSection : childSection.getOrCreateSection(parts[1]);
    }

    @Override
    public Set<String> keys() {
        Set<String> keys = new LinkedHashSet<>();
        node.fieldNames().forEachRemaining(keys::add);
        return Collections.unmodifiableSet(keys);
    }

    @Override
    public boolean contains(String dotPath) {
        return !node(dotPath).isNull();
    }


    @Override
    public <T> @Nullable T get(String dotPath, Class<T> type, @Nullable T defaultValue) {
        return node(dotPath).get(type, defaultValue);
    }

    @Override
    public int get(String dotPath, int defaultValue) {
        return node(dotPath).getInt(defaultValue);
    }

    @Override
    public long get(String dotPath, long defaultValue) {
        return node(dotPath).getLong(defaultValue);
    }

    @Override
    public double get(String dotPath, double defaultValue) {
        return node(dotPath).getDouble(defaultValue);
    }

    @Override
    public boolean get(String dotPath, boolean defaultValue) {
        return node(dotPath).getBoolean(defaultValue);
    }

    @Override
    public String get(String dotPath, String defaultValue) {
        return node(dotPath).getString(defaultValue);
    }

    @Override
    public <T> T require(String dotPath, Class<T> type) throws ConfigException {
        return node(dotPath).require(type);
    }

    @Override
    public <T> Optional<T> optional(String dotPath, Class<T> type) {
        return node(dotPath).optional(type);
    }

    @Override
    public <T> List<T> getList(String dotPath, Class<T> elementType) {
        return node(dotPath).getList(elementType);
    }


    @Override
    public <T> T get(Class<T> type, @Nullable T defaultValue) {
        try {
            return SerializationManager.SERIALIZER.treeToValue(node, type);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String getString(String defaultValue) {
        return defaultValue;
    }

    @Override
    public int getInt(int defaultValue) {
        return defaultValue;
    }

    @Override
    public long getLong(long defaultValue) {
        return defaultValue;
    }

    @Override
    public double getDouble(double defaultValue) {
        return defaultValue;
    }

    @Override
    public boolean getBoolean(boolean defaultValue) {
        return defaultValue;
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
    public String requireString() throws ConfigException {
        throw ConfigException.notASection(path + " (is a section, not a string)");
    }

    @Override
    public <T> Optional<T> optional(Class<T> type) {
        try {
            return Optional.ofNullable(SerializationManager.SERIALIZER.treeToValue(node, type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> List<T> getList(Class<T> elementType) {
        return Collections.emptyList();
    }


    @Override
    public void set(@Nullable Object value) throws ConfigException {
        // Setting a section-level value replaces the whole node- unusual but supported
        if (value == null) {
            node.removeAll();
            return;
        }
        try {
            JsonNode tree = SerializationManager.SERIALIZER.valueToTree(value);
            if (!tree.isObject()) {
                throw ConfigException.serializationFailed(path, new IllegalArgumentException("Value must serialize to an object"));
            }
            node.removeAll();
            node.setAll((ObjectNode)tree);
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            throw ConfigException.serializationFailed(path, e);
        }
    }

    @Override
    public void set(String dotPath, @Nullable Object value) throws ConfigException {
        String[] parts = dotPath.split("\\.", 2);

        if (parts.length == 1) {
            // Leaf write
            if (value == null) {
                node.remove(parts[0]);
                return;
            }
            try {
                node.set(parts[0], SerializationManager.SERIALIZER.valueToTree(value));
            } catch (Exception e) {
                throw ConfigException.serializationFailed(dotPath, e);
            }
        } else {
            // Recurse- auto-create intermediate sections
            getOrCreateSection(parts[0]).set(parts[1], value);
        }
    }

    @Override
    public void remove(String dotPath) {
        String[] parts = dotPath.split("\\.", 2);
        if (parts.length == 1) {
            node.remove(parts[0]);
        } else {
            ConfigNode child = node(parts[0]);
            if (child.isSection()) {
                try {
                    child.asSection().remove(parts[1]);
                } catch (ConfigException ignored) {}
            }
        }
    }

    @Override
    public ConfigSection asSection() {
        return this;
    }


    private String childPath(String childKey) {
        return path.isEmpty() ? childKey : path + "." + childKey;
    }

}