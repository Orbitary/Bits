/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.util.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;


/**
 * A collection of utilities for working with entities.
 */
public final class Entities {
    private Entities() {}

    public static <T extends Entity> Optional<T> getNearestEntity(Location location, Class<T> entityClass) {
        return getNearestEntity(location, entityClass, entity -> true);
    }

    public static <T extends Entity> Optional<T> getNearestEntity(Location location, Class<T> entityClass, Predicate<T> filter) {
        if (location == null || location.getWorld() == null) return Optional.empty();

        Collection<T> entities = location.getWorld().getEntitiesByClass(entityClass);

        return entities.stream()
          .filter(filter)
          .min(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(location)));
    }

}