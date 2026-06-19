/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.wrapper;

import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.Objects;


/**
 * A representation of a Bukkit world definition. May be currently loaded or unloaded.
 *
 * @since 0.0.20
 */
public final class WorldDefinition {
    public final NamespacedKey key;

    private WorldDefinition(NamespacedKey key) {
        this.key = key;
    }

    public static WorldDefinition of(Key key) {
        return new WorldDefinition(new NamespacedKey(key.namespace(), key.value()));
    }


    public World load() {
        World loaded = Bukkit.getWorld(key);
        if (loaded != null) return loaded;

        return Objects.requireNonNull(Bukkit.createWorld(WorldCreator.ofKey(key)), "Failed to load world with key: " + key);
    }

    public void unload(final boolean save) {
        World loaded = Bukkit.getWorld(key);
        if (loaded != null) Bukkit.unloadWorld(loaded, save);
    }


}
