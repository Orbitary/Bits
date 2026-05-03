/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.configuration.loader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import xyz.bitsquidd.bits.configuration.ConfigException;
import xyz.bitsquidd.bits.configuration.node.ConfigSection;
import xyz.bitsquidd.bits.configuration.node.impl.JacksonConfigSection;
import xyz.bitsquidd.bits.util.serializer.SerializationManager;

import java.io.File;
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
public final class YamlConfigLoader implements ConfigLoader {

    private static final YAMLMapper YAML_MAPPER = YAMLMapper.builder(
      YAMLFactory.builder()
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
        .build()
    ).build();

    private final File file;

    /**
     * Creates a YAML loader backed by the given file.
     * The file does not need to exist yetL {@link #load()} returns an empty section
     * if it is missing, and {@link #save(ConfigSection)} creates it on first write.
     *
     * @param file the YAML file to read/write
     *
     * @since 0.0.14
     */
    public YamlConfigLoader(File file) {
        this.file = file;
    }

    @Override
    public ConfigSection load() throws ConfigException {
        if (!file.exists()) {
            // First runL return empty section so defaults can be applied
            return JacksonConfigSection.root(SerializationManager.SERIALIZER.createObjectNode());
        }

        try {
            ObjectNode node = (ObjectNode)YAML_MAPPER.readTree(file);
            // readTree returns null for an empty file
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
              new IllegalArgumentException("YamlConfigLoader can only save JacksonConfigSection instances")
            );
        }

        try {
            // Create parent directories if missing
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            YAML_MAPPER.writeValue(file, jacksonSection.objectNode());
        } catch (IOException e) {
            throw ConfigException.saveFailed(file.getPath(), e);
        }
    }

    @Override
    public String formatName() {
        return "YAML";
    }

    /**
     * Returns the backing file this loader reads from and writes to.
     */
    public File file() {
        return file;
    }

}