/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.config.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.orbitary.bits.config.ConfigException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {
    @TempDir Path directory;

    private static ConfigLoader stream(boolean yaml, String content) {
        var input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return yaml ? ConfigLoader.yaml(input) : ConfigLoader.json(input);
    }

    @Test
    void missingFilesLoadEmptyAndFirstSaveCreatesParentDirectories() throws Exception {
        for (boolean yaml : List.of(false, true)) {
            var path = directory.resolve(yaml ? "yaml/nested/settings.yml" : "json/nested/settings.json");
            var loader = yaml ? ConfigLoader.yaml(path) : ConfigLoader.json(path);
            var config = loader.load();
            assertTrue(config.keys().isEmpty());
            assertFalse(Files.exists(path));
            config.set("server.name", "Biome café 🌍");
            config.set("server.players", List.of("alice", "bob"));
            loader.save(config);
            assertTrue(Files.isRegularFile(path));
            var reloaded = loader.load();
            assertEquals("Biome café 🌍", reloaded.require("server.name", String.class));
            assertEquals(List.of("alice", "bob"), reloaded.getList("server.players", String.class));
        }
    }

    @Test
    void streamsParseNestedValuesAndCannotBeSaved() throws Exception {
        for (boolean yaml : List.of(false, true)) {
            var loader = stream(yaml, yaml ? "server:\n  port: 25565\n" : "{\"server\":{\"port\":25565}}");
            var config = loader.load();
            assertEquals(25565, config.require("server.port", Integer.class));
            assertThrows(UnsupportedOperationException.class, () -> loader.save(config));
        }
    }

    @Test
    void emptyStreamsProduceEmptySections() throws Exception {
        for (boolean yaml : List.of(false, true)) {
            assertTrue(stream(yaml, "").load().keys().isEmpty());
        }
    }

    @Test
    void jsonRootsMustBeObjects() {
        for (var document : List.of("[]", "42", "true", "null", "\"scalar\"")) {
            assertThrows(ConfigException.class, () -> stream(false, document).load());
        }
    }

    @Test
    void malformedDocumentsAreReportedAsConfigExceptions() {
        assertThrows(ConfigException.class, () -> stream(false, "{\"broken\":").load());
        assertThrows(ConfigException.class, () -> stream(true, "broken: [").load());
    }
}
