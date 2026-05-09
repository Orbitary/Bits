/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config.migration;

import xyz.bitsquidd.bits.config.ConfigException;
import xyz.bitsquidd.bits.config.node.ConfigSection;


/**
 * Represents a single step in a configuration migration chain.
 * <p>
 * When a configuration is loaded, its {@code config-version} key is compared to
 * the current expected version. If behind, all registered migrations whose
 * {@link #fromVersion()} matches the stored version are applied in sequence until
 * the config is up to date.
 * <p>
 * A missing {@code config-version} key is treated as version {@code 0}, so all
 * migrations are applied on first run.
 * <p>
 * Example: renaming a key between v1 and v2:
 * <pre>{@code
 * public class V1ToV2Migration implements ConfigMigration {
 *
 *     @Override
 *     public int fromVersion() { return 1; }
 *
 *     @Override
 *     public void migrate(ConfigSection config) throws ConfigException {
 *         // Move old-key to its new location
 *         String oldValue = config.get("settings.old-key", "default");
 *         config.set("settings.new-key", oldValue);
 *         config.remove("settings.old-key");
 *     }
 * }
 * }</pre>
 *
 * @since 0.0.14
 */
public interface ConfigMigration {

    /**
     * The version this migration upgrades <em>from</em>.
     * <p>
     * A migration registered with {@code fromVersion() == 2} upgrades a
     * version-2 config to version 3.
     *
     * @since 0.0.14
     */
    int fromVersion();

    /**
     * Applies this migration to the given section in-place.
     * <p>
     * The {@code config-version} key is updated automatically after this
     * method returns- do not set it manually.
     *
     * @param config the configuration section to migrate
     *
     * @throws ConfigException if the migration cannot complete
     * @since 0.0.14
     */
    void migrate(ConfigSection config) throws ConfigException;

    /**
     * Optional human-readable description of what this migration does.
     * Used in log output when the migration runs.
     *
     * @since 0.0.14
     */
    default String description() {
        return "v" + fromVersion() + " → v" + (fromVersion() + 1);
    }

}