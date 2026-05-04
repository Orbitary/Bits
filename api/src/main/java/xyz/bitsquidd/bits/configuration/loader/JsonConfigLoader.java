/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.configuration.loader;

import com.fasterxml.jackson.databind.node.ObjectNode;

import xyz.bitsquidd.bits.configuration.ConfigException;
import xyz.bitsquidd.bits.configuration.node.ConfigSection;
import xyz.bitsquidd.bits.configuration.node.impl.JacksonConfigSection;
import xyz.bitsquidd.bits.util.serializer.SerializationManager;

import java.io.File;
import java.io.IOException;

/**
 * A {@link ConfigLoader} that reads and writes JSON files.
 * <p>
 * Since Jackson is already the core serialization dependency, this is a thin
 * wrapper that reads the file directly into an {@link ObjectNode}.
 * <p>
 * Pretty-printing is enabled by default for human readability.
 *
 * @since 0.0.14
 */
public final class JsonConfigLoader implements ConfigLoader {

    private final File file;

    /**
     * Creates a JSON loader backed by the given file.
     *
     * @param file the JSON file to read/write
     *
     * @since 0.0.14
     */
    public JsonConfigLoader(File file) {
        this.file = file;
    }

    @Override
    public ConfigSection load() throws ConfigException {
        if (!file.exists()) {
            return JacksonConfigSection.root(SerializationManager.SERIALIZER.createObjectNode());
        }

        try {
            ObjectNode node = (ObjectNode)SerializationManager.SERIALIZER.readTree(file);
            if (node == null) node = SerializationManager.SERIALIZER.createObjectNode();
            return JacksonConfigSection.root(node);
        } catch (IOException e) {
            throw ConfigException.loadFailed(file.getPath(), e);
        }
    }

    @Override
    public void save(ConfigSection section) throws ConfigException {
        if (!(section instanceof JacksonConfigSection jacksonSection)) {
            throw ConfigException.saveFailed(
              file.getPath(),
              new IllegalArgumentException("JsonConfigLoader can only save JacksonConfigSection instances")
            );
        }

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            SerializationManager.SERIALIZER
              .writerWithDefaultPrettyPrinter()
              .writeValue(file, jacksonSection.objectNode());
        } catch (IOException e) {
            throw ConfigException.saveFailed(file.getPath(), e);
        }
    }

    @Override
    public String formatName() {
        return "JSON";
    }

    /**
     * Returns the backing file this loader reads from and writes to.
     */
    public File file() {
        return file;
    }

}