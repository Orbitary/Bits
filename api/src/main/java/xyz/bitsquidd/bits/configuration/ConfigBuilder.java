/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.configuration;

import xyz.bitsquidd.bits.config.BitsConfig;
import xyz.bitsquidd.bits.configuration.loader.ConfigLoader;
import xyz.bitsquidd.bits.configuration.migration.ConfigMigration;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for creating a {@link BitsConfig}.
 * <p>
 * This is the primary entry point for both internal and third-party plugin use.
 * <p>
 * Example- minimal YAML config with no migrations:
 * <pre>{@code
 * BitsConfig config = ConfigBuilder.create()
 *     .loader(new YamlConfigLoader(file))
 *     .build();
 * config.reload();
 * }</pre>
 * <p>
 * Example- versioned config with migrations:
 * <pre>{@code
 * BitsConfig config = ConfigBuilder.create()
 *     .loader(new YamlConfigLoader(file))
 *     .currentVersion(3)
 *     .migration(new V1ToV2Migration())
 *     .migration(new V2ToV3Migration())
 *     .versionKey("config-version")   // optional, this is the default
 *     .build();
 * config.reload();
 * }</pre>
 *
 * @since 0.0.14
 */
public final class ConfigBuilder {
    public static final String DEFAULT_VERSION_KEY = "config-version"; // Key used to store the config schema version

    private final ConfigLoader loader;
    private int currentVersion = 0;
    private String versionKey = DEFAULT_VERSION_KEY;
    private final List<ConfigMigration> migrations = new ArrayList<>();

    private ConfigBuilder(ConfigLoader loader) {
        this.loader = loader;
    }

    /**
     * Creates a new, empty {@link ConfigBuilder}.
     *
     * @since 0.0.14
     */
    public static ConfigBuilder create(ConfigLoader loader) {
        return new ConfigBuilder(loader);
    }

    /**
     * Sets the current expected schema version of the configuration.
     * <p>
     * When a config is loaded, its stored version is compared to this value.
     * Any registered migrations whose {@link ConfigMigration#fromVersion()} falls
     * between the stored version and this value (inclusive) are applied in order.
     * <p>
     * Defaults to {@code 0} (no versioning). Set this whenever you add your first
     * migration.
     *
     * @param version the current version, must be >= 0
     *
     * @since 0.0.14
     */
    public ConfigBuilder currentVersion(int version) {
        if (version < 0) throw new IllegalArgumentException("Version must be >= 0, got: " + version);
        this.currentVersion = version;
        return this;
    }

    /**
     * Overrides the YAML/JSON key used to store the schema version inside the file.
     * Defaults to {@value DEFAULT_VERSION_KEY}.
     *
     * @param key the version key
     *
     * @since 0.0.14
     */
    public ConfigBuilder versionKey(String key) {
        this.versionKey = key;
        return this;
    }

    /**
     * Registers a migration step.
     * <p>
     * Migrations are sorted by {@link ConfigMigration#fromVersion()} automatically
     * - registration order does not matter.
     *
     * @param migration the migration to register
     *
     * @since 0.0.14
     */
    public ConfigBuilder migration(ConfigMigration migration) {
        this.migrations.add(migration);
        return this;
    }

}