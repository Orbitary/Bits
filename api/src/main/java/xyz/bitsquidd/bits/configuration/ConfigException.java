/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.configuration;

/**
 * Base exception for all configuration failures.
 *
 * @since 0.0.14
 */
public class ConfigException extends Exception {

    private ConfigException(String message) {
        super(message);
    }

    private ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * The path exists but cannot be converted to the requested type.
     *
     * @since 0.0.14
     */
    public static ConfigException typeMismatch(String path, Class<?> expected, Throwable cause) {
        return new ConfigException("Cannot read '" + path + "' as " + expected.getSimpleName() + ": " + cause.getMessage(), cause);
    }

    /**
     * The path is absent or null and a value was required.
     *
     * @since 0.0.14
     */
    public static ConfigException missingValue(String path) {
        return new ConfigException("Required config value is missing: '" + path + "'");
    }

    /**
     * The path exists but is not a section (map), and one was expected.
     *
     * @since 0.0.14
     */
    public static ConfigException notASection(String path) {
        return new ConfigException("Expected a section (map) at '" + path + "' but found a scalar value");
    }

    /**
     * The backing source (e.g. file) could not be read.
     *
     * @since 0.0.14
     */
    public static ConfigException loadFailed(String source, Throwable cause) {
        return new ConfigException("Failed to load config from '" + source + "': " + cause.getMessage(), cause);
    }

    /**
     * The backing source could not be written.
     *
     * @since 0.0.14
     */
    public static ConfigException saveFailed(String destination, Throwable cause) {
        return new ConfigException("Failed to save config to '" + destination + "': " + cause.getMessage(), cause);
    }

    /**
     * A value could not be serialized for writing.
     *
     * @since 0.0.14
     */
    public static ConfigException serializationFailed(String path, Throwable cause) {
        return new ConfigException("Failed to serialize value at '" + path + "': " + cause.getMessage(), cause);
    }

    /**
     * A migration step failed.
     *
     * @since 0.0.14
     */
    public static ConfigException migrationFailed(int fromVersion, String description, Throwable cause) {
        return new ConfigException("Migration failed (" + description + ", from version " + fromVersion + "): " + cause.getMessage(), cause);
    }

    /**
     * The {@link xyz.bitsquidd.bits.configuration.ConfigBuilder} is misconfigured.
     *
     * @since 0.0.14
     */
    public static ConfigException invalidBuilder(String reason) {
        return new ConfigException("Invalid ConfigBuilder: " + reason);
    }

}