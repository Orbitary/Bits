/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.config.loader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import xyz.bitsquidd.bits.config.ConfigException;
import xyz.bitsquidd.bits.config.node.ConfigSection;
import xyz.bitsquidd.bits.config.node.impl.JacksonConfigSection;
import xyz.bitsquidd.bits.util.serializer.SerializationManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * A {@link ConfigLoader} that reads and writes YAML files.
 * <p>
 * Internally converts YAML → Jackson {@link ObjectNode} using Jackson's
 * {@code jackson-dataformat-yaml} module, then delegates all typed access
 * to {@code SerializationManager.SERIALIZER}.
 * <p>
 * The YAML writer is configured to:
 * <ul>
 *   <li>Suppress document start markers ({@code ---})</li>
 *   <li>Write strings unquoted where possible</li>
 *   <li>Preserve insertion order of keys</li>
 * </ul>
 *
 * @since 0.0.14
 */
public abstract sealed class YamlConfigLoader implements ConfigLoader permits YamlConfigLoader.File, YamlConfigLoader.InputStream {

    private static final YAMLMapper YAML_MAPPER = YAMLMapper.builder(
      YAMLFactory.builder()
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
        .build()
    ).build();

    private YamlConfigLoader() {}

    static ObjectNode readTree(java.io.InputStream source, String label) throws ConfigException {
        try {
            com.fasterxml.jackson.databind.JsonNode raw = YAML_MAPPER.readTree(source);
            if (raw == null || !raw.isObject()) return SerializationManager.SERIALIZER.createObjectNode();
            return (ObjectNode) raw;
        } catch (IOException e) {
            throw ConfigException.loadFailed(label, e);
        }
    }

    @Override
    public String formatName() {
        return "YAML";
    }


    /**
     * A {@link YamlConfigLoader} backed by a file on disk.
     * <p>
     * If the file does not exist, {@link #load()} returns an empty section so
     * defaults can be applied. {@link #save(ConfigSection)} creates the file
     * (and any missing parent directories) on first write.
     *
     * @since 0.0.14
     */
    public static final class File extends YamlConfigLoader {
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
                  new IllegalArgumentException("YamlConfigLoader can only save JacksonConfigSection instances")
                );
            }

            try {
                java.io.File parent = file.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();
                YAML_MAPPER.writeValue(file, jacksonSection.objectNode());
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
     * A read-only {@link YamlConfigLoader} backed by an {@link java.io.InputStream}.
     * Intended for classpath/bundled resources (e.g. fat-JAR defaults).
     * <p>
     * {@link #save(ConfigSection)} throws {@link UnsupportedOperationException}.
     *
     * @since 0.0.14
     */
    public static final class InputStream extends YamlConfigLoader {
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
            throw new UnsupportedOperationException("YamlConfigLoader.InputStream is read-only");
        }

    }

}
