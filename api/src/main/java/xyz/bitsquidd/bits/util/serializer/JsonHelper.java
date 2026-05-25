/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.serializer;

import java.io.IOException;

import static xyz.bitsquidd.bits.util.serializer.SerializationManager.SERIALIZER;


public final class JsonHelper {
    private JsonHelper() {}

    /**
     * Reads a single top-level text field from a JSON string.
     *
     * @param json     the raw JSON string
     * @param field    the field name to extract
     * @param fallback returned when the field is absent or the JSON is invalid
     * @return the field value as a string, or {@code fallback}
     */
    public static String extractText(String json, String field, String fallback) {
        try {
            return SERIALIZER.readTree(json).path(field).asText(fallback);
        } catch (IOException e) {
            return fallback;
        }
    }

}
