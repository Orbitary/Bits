/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config.loader;

import com.fasterxml.jackson.databind.node.ObjectNode;

import xyz.bitsquidd.bits.config.ConfigException;
import xyz.bitsquidd.bits.config.node.ConfigSection;
import xyz.bitsquidd.bits.config.node.impl.JacksonConfigSection;
import xyz.bitsquidd.bits.util.serializer.SerializationManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * A {@link ConfigLoader} that reads and writes JSON files.
 * <p>
 * Since Jackson is already the core serialization dependency, this is a thin
 * wrapper that reads the file directly into an {@link ObjectNode}.
 * <p>
 * Pretty-printing is enabled by default for human readability.
 * <p>
 *
 * @since 0.0.14
 */
public abstract sealed class JsonConfigLoader implements ConfigLoader permits JsonConfigLoader.File, JsonConfigLoader.InputStream {

    private JsonConfigLoader() {}

    static ObjectNode readTree(java.io.InputStream source, String label) throws ConfigException {
        try {
            ObjectNode node = (ObjectNode)SerializationManager.SERIALIZER.readTree(source);
            if (node == null) node = SerializationManager.SERIALIZER.createObjectNode();
            return node;
        } catch (IOException e) {
            throw ConfigException.loadFailed(label, e);
        }
    }

    @Override
    public String formatName() {
        return "JSON";
    }


    /**
     * A {@link JsonConfigLoader} backed by a file on disk.
     * <p>
     * If the file does not exist, {@link #load()} returns an empty section so
     * defaults can be applied. {@link #save(ConfigSection)} creates the file
     * (and any missing parent directories) on first write.
     *
     * @since 0.0.14
     */
    public static final class File extends JsonConfigLoader {
        private final java.io.File file;

        File(java.io.File file) {
            this.file = file;
        }

        @Override
        public ConfigSection load() throws ConfigException {
            java.io.InputStream source;
            try {
                source = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                return JacksonConfigSection.root(SerializationManager.SERIALIZER.createObjectNode());
            }
            return JacksonConfigSection.root(readTree(source, file.getPath()));
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
                java.io.File parent = file.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();
                SerializationManager.SERIALIZER
                  .writerWithDefaultPrettyPrinter()
                  .writeValue(file, jacksonSection.objectNode());
            } catch (IOException e) {
                throw ConfigException.saveFailed(file.getPath(), e);
            }
        }

        /**
         * Returns the backing file.
         */
        public java.io.File file() {
            return file;
        }

    }

    /**
     * A read-only {@link JsonConfigLoader} backed by an {@link java.io.InputStream}.
     * Intended for classpath/bundled resources (e.g. fat-JAR defaults).
     * <p>
     * {@link #save(ConfigSection)} throws {@link UnsupportedOperationException}.
     *
     * @since 0.0.14
     */
    public static final class InputStream extends JsonConfigLoader {

        private final java.io.InputStream stream;

        InputStream(java.io.InputStream stream) {
            this.stream = stream;
        }

        @Override
        public ConfigSection load() throws ConfigException {
            return JacksonConfigSection.root(readTree(stream, "<stream>"));
        }

        @Override
        public void save(ConfigSection section) {
            throw new UnsupportedOperationException("JsonConfigLoader.InputStream is read-only");
        }

    }

}
