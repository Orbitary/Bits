/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.configuration.node;

import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.configuration.ConfigException;

import java.util.List;
import java.util.Optional;

/**
 * Represents a single value node within a configuration tree.
 * <p>
 * A node can hold a scalar value (string, int, boolean, etc.), a list,
 * or act as a section (map) containing child nodes.
 * <p>
 * Typed reads delegate to the registered Jackson {@code ObjectMapper}, meaning
 * any type with a registered {@code MultiSerializer} is automatically supported.
 *
 * @since 0.0.14
 */
public interface ConfigNode {

    /**
     * Returns the key this node was accessed under within its parent section.
     * The root node has a key of {@code ""}.
     *
     * @since 0.0.14
     */
    String key();

    /**
     * Returns the full dot-separated path from the root to this node.
     * Example: {@code "database.connection.timeout"}
     *
     * @since 0.0.14
     */
    String path();

    /**
     * Returns whether this node has no value (null or absent).
     *
     * @since 0.0.14
     */
    boolean isNull();

    /**
     * Returns whether this node contains child nodes (i.e. is a map/section).
     *
     * @since 0.0.14
     */
    boolean isSection();

    /**
     * Returns whether this node holds a list value.
     *
     * @since 0.0.14
     */
    boolean isList();


    /**
     * Reads the node value as the given type, falling back to {@code defaultValue}
     * if the node is absent or cannot be converted.
     * <p>
     * Conversion is handled by {@code SerializationManager.SERIALIZER}, so any
     * type with a registered Jackson serializer is supported automatically.
     *
     * @param type         the target type
     * @param defaultValue fallback value
     * @param <T>          the value type
     *
     * @since 0.0.14
     */
    <T> T get(Class<T> type, @Nullable T defaultValue);

    /**
     * Reads the node as a {@code String}, or returns {@code defaultValue}.
     */
    String getString(String defaultValue);

    /**
     * Reads the node as an {@code int}, or returns {@code defaultValue}.
     */
    int getInt(int defaultValue);

    /**
     * Reads the node as a {@code long}, or returns {@code defaultValue}.
     */
    long getLong(long defaultValue);

    /**
     * Reads the node as a {@code double}, or returns {@code defaultValue}.
     */
    double getDouble(double defaultValue);

    /**
     * Reads the node as a {@code boolean}, or returns {@code defaultValue}.
     */
    boolean getBoolean(boolean defaultValue);


    /**
     * Reads the node value as the given type.
     *
     * @param type the target type
     * @param <T>  the value type
     *
     * @throws ConfigException if the node is absent or conversion fails
     * @since 0.0.14
     */
    <T> T require(Class<T> type) throws ConfigException;

    /**
     * Reads the node as a {@code String}.
     *
     * @throws ConfigException if absent
     * @since 0.0.14
     */
    String requireString() throws ConfigException;


    /**
     * Reads the node value as the given type, wrapped in an {@link Optional}.
     *
     * @param type the target type
     * @param <T>  the value type
     *
     * @since 0.0.14
     */
    <T> Optional<T> optional(Class<T> type);


    /**
     * Reads the node as a {@code List} of the given element type.
     *
     * @param elementType the list element type
     * @param <T>         the element type
     *
     * @since 0.0.14
     */
    <T> List<T> getList(Class<T> elementType);


    /**
     * Sets the value of this node.
     * <p>
     * The value is serialized via {@code SerializationManager.SERIALIZER},
     * so any registered type is supported.
     *
     * @param value the value to set, or {@code null} to clear the node
     *
     * @throws ConfigException if serialization fails
     * @since 0.0.14
     */
    void set(@Nullable Object value) throws ConfigException;


    /**
     * Returns this node as a {@link ConfigSection}.
     *
     * @throws ConfigException if this node is not a section
     * @since 0.0.14
     */
    ConfigSection asSection() throws ConfigException;

}