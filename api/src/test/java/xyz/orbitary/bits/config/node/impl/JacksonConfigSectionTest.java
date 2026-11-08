/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.config.node.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import xyz.orbitary.bits.config.ConfigException;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigSectionTest {
    private final JacksonConfigSection root = JacksonConfigSection.root(new ObjectMapper().createObjectNode());

    @Test
    void nestedWritesCreateSectionsAndPreserveSiblingValues() throws Exception {
        root.set("database.host", "localhost");
        root.set("database.port", 5432);
        var database = root.section("database");
        assertEquals("database", database.path());
        assertEquals("database", database.key());
        assertEquals("database.port", database.node("port").path());
        assertEquals("localhost", database.require("host", String.class));
        assertEquals(5432, root.require("database.port", Integer.class));
    }

    @Test
    void sectionViewsWriteThroughToTheirParent() throws Exception {
        var database = root.getOrCreateSection("database");
        database.set("timeout", 30);
        assertEquals(30, root.require("database.timeout", Integer.class));
        root.set("database.timeout", 60);
        assertEquals(60, database.require("timeout", Integer.class));
    }

    @Test
    void absentAndInvalidReadsUseDefaultsButRequiredReadsFail() throws Exception {
        assertTrue(root.get("missing", Integer.class).isEmpty());
        assertEquals(30, root.getOrDefault("missing", Integer.class, 30));
        assertThrows(ConfigException.class, () -> root.require("missing", Integer.class));
        root.set("timeout", "not a number");
        assertEquals(30, root.getOrDefault("timeout", Integer.class, 30));
        assertThrows(ConfigException.class, () -> root.require("timeout", Integer.class));
        assertThrows(ConfigException.class, () -> root.section("timeout"));
        assertThrows(ConfigException.class, () -> root.section("missing"));
    }

    @Test
    void nullWritesAndRemovalDeleteOnlyTheirTarget() throws Exception {
        root.set("database.host", "localhost");
        root.set("database.port", 5432);
        root.set("database.host", null);
        assertFalse(root.contains("database.host"));
        assertEquals(5432, root.require("database.port", Integer.class));
        root.remove("database.port");
        root.remove("missing.child");
        assertTrue(root.section("database").keys().isEmpty());
    }

    @Test
    void typedListsRejectInvalidElementsWithoutReturningAPartialList() throws Exception {
        root.set("values", List.of(1, 2, 3));
        assertTrue(root.node("values").isList());
        assertEquals(List.of(1, 2, 3), root.getList("values", Integer.class));
        root.set("values", List.of("1", "invalid", "3"));
        assertTrue(root.getList("values", Integer.class).isEmpty());
        assertTrue(root.getList("missing", Integer.class).isEmpty());
    }

    @Test
    void keysAreAnImmutableSnapshotInInsertionOrder() throws Exception {
        root.set("first", 1);
        root.set("second", 2);
        var keys = root.keys();
        root.set("third", 3);
        assertEquals(List.of("first", "second"), List.copyOf(keys));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove("first"));
    }

    @Test
    void failedSectionReplacementPreservesExistingValues() throws Exception {
        root.set("original", 42);
        assertThrows(ConfigException.class, () -> root.set("scalar"));
        assertEquals(42, root.require("original", Integer.class));
        root.set(Map.of("replacement", true));
        assertFalse(root.contains("original"));
        assertEquals(true, root.require("replacement", Boolean.class));
        root.set(null);
        assertTrue(root.keys().isEmpty());
    }
}
