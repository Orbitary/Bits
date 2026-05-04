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
import java.util.Set;

/**
 * A {@link ConfigNode} that contains child nodes, representing a YAML/JSON map.
 * <p>
 * This is the primary interface for navigating a configuration tree.
 * All path arguments use dot-notation: {@code "database.connection.timeout"}.
 * <p>
 * Example usage:
 * <pre>{@code
 * ConfigSection config = builder.build().load();
 *
 * int timeout   = config.get("database.timeout", 30);
 * String host   = config.require("database.host", String.class);
 * Location spawn = config.get("spawn", Location.class, null);
 *
 * ConfigSection db = config.section("database");
 * }</pre>
 *
 * @since 0.0.14
 */
public interface ConfigSection extends ConfigNode {

    /**
     * Returns the child node at the given dot-separated path.
     * If the path does not exist, returns an absent (null) node rather than throwing.
     *
     * @param path dot-separated path
     *
     * @since 0.0.14
     */
    ConfigNode node(String path);

    /**
     * Returns the child section at the given dot-separated path.
     *
     * @param path dot-separated path
     *
     * @return the section at the path
     *
     * @throws ConfigException if the path does not exist or is not a section
     * @since 0.0.14
     */
    ConfigSection section(String path) throws ConfigException;

    /**
     * Returns the child section at the given path, creating it if absent.
     *
     * @param path dot-separated path
     *
     * @since 0.0.14
     */
    ConfigSection getOrCreateSection(String path);

    /**
     * Returns the keys of direct children in this section.
     *
     * @since 0.0.14
     */
    Set<String> keys();

    /**
     * Returns whether this section contains a value at the given path.
     *
     * @param path dot-separated path
     *
     * @since 0.0.14
     */
    boolean contains(String path);


    /**
     * Reads the value at {@code path} as the given type, or returns {@code defaultValue}.
     *
     * @param path         dot-separated path
     * @param type         the target type
     * @param defaultValue fallback value
     * @param <T>          the value type
     *
     * @since 0.0.14
     */
    <T> @Nullable T get(String path, Class<T> type, @Nullable T defaultValue);

    /**
     * Reads the value at {@code path} as {@code int}, or returns {@code defaultValue}.
     *
     * @since 0.0.14
     */
    int get(String path, int defaultValue);

    /**
     * Reads the value at {@code path} as {@code long}, or returns {@code defaultValue}.
     *
     * @since 0.0.14
     */
    long get(String path, long defaultValue);

    /**
     * Reads the value at {@code path} as {@code double}, or returns {@code defaultValue}.
     *
     * @since 0.0.14
     */
    double get(String path, double defaultValue);

    /**
     * Reads the value at {@code path} as {@code boolean}, or returns {@code defaultValue}.
     *
     * @since 0.0.14
     */
    boolean get(String path, boolean defaultValue);

    /**
     * Reads the value at {@code path} as {@code String}, or returns {@code defaultValue}.
     *
     * @since 0.0.14
     */
    String get(String path, String defaultValue);

    /**
     * Reads the value at {@code path} as the given type.
     *
     * @throws ConfigException if absent or conversion fails
     * @since 0.0.14
     */
    <T> T require(String path, Class<T> type) throws ConfigException;

    /**
     * Reads the value at {@code path} as the given type, wrapped in an {@link Optional}.
     *
     * @since 0.0.14
     */
    <T> Optional<T> optional(String path, Class<T> type);

    /**
     * Reads the value at {@code path} as a {@code List} of the given element type.
     *
     * @since 0.0.14
     */
    <T> List<T> getList(String path, Class<T> elementType);


    /**
     * Sets the value at the given dot-separated path.
     * Intermediate sections are created automatically.
     *
     * @param path  dot-separated path
     * @param value the value to set, or {@code null} to remove
     *
     * @throws ConfigException if serialization fails
     * @since 0.0.14
     */
    void set(String path, @Nullable Object value) throws ConfigException;

    /**
     * Removes the node at the given path, if present.
     *
     * @param path dot-separated path
     *
     * @since 0.0.14
     */
    void remove(String path);

}