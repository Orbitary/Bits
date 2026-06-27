/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

import java.util.Locale;


public final class Keys {
    private Keys() {}

    public static NamespacedKey NSK(final Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }

    public static String sanitize(String key) {
        // Normalize to lower\-case and strip surrounding whitespace
        String sanitized = key.trim().toLowerCase(Locale.ROOT);

        // Replace any sequence of invalid characters with a single underscore
        sanitized = sanitized.replaceAll("[^a-z0-9_]+", "_");

        // Remove leading/trailing underscores
        sanitized = sanitized.replaceAll("^_+|_+$", "");

        return sanitized;
    }

}
