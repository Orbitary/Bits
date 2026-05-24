/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config.loader;

import xyz.bitsquidd.bits.config.ConfigException;
import xyz.bitsquidd.bits.config.node.ConfigSection;


/**
 * Responsible for reading and writing a {@link ConfigSection} from/to a backing source.
 * <p>
 * Implementations exist per format (YAML, JSON). Platform-specific loaders
 * (e.g. {@code PaperConfigLoader}) may add features like hot-reload or plugin
 * data folder resolution on top of a core loader.
 *
 * @since 0.0.14
 */
public interface ConfigLoader {

    /**
     * Loads the configuration from the backing source and returns it as a
     * {@link ConfigSection}.
     * <p>
     * If the backing source does not exist (e.g. the file is missing), an empty
     * section is returned rather than throwing: this allows first-run defaults
     * to be written cleanly.
     *
     * @throws ConfigException if the source exists but cannot be parsed
     * @since 0.0.14
     */
    ConfigSection load() throws ConfigException;

    /**
     * Writes the given {@link ConfigSection} back to the backing source.
     *
     * @throws ConfigException if the write fails
     * @since 0.0.14
     */
    void save(ConfigSection section) throws ConfigException;

    /**
     * Returns the human-readable name of this loader's format, e.g. {@code "YAML"}.
     * Used in error messages.
     *
     * @since 0.0.14
     */
    String formatName();


    //region Static factories
    static ConfigLoader yaml(java.io.InputStream source) {
        return new YamlConfigLoader.InputStream(source);
    }

    static ConfigLoader yaml(java.io.File file) {
        return new YamlConfigLoader.File(file);
    }

    static ConfigLoader yaml(java.nio.file.Path path) {
        return yaml(path.toFile());
    }


    static ConfigLoader json(java.io.InputStream source) {
        return new JsonConfigLoader.InputStream(source);
    }

    static ConfigLoader json(java.io.File file) {
        return new JsonConfigLoader.File(file);
    }

    static ConfigLoader json(java.nio.file.Path path) {
        return json(path.toFile());
    }
    //endregion


}