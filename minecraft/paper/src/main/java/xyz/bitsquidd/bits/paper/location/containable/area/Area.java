/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.area;

import com.google.common.collect.ImmutableList;
import org.bukkit.World;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;
import xyz.bitsquidd.bits.paper.location.Locations;
import xyz.bitsquidd.bits.paper.location.containable.Containable;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;
import xyz.bitsquidd.bits.paper.location.wrapper.Locatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * An immutable boolean composition of {@link Containable}s (Regions or other Areas),
 * evaluated left-to-right as a sequence of set operations.
 *
 * <pre>{@code
 * Area arena = Area.from(floor)
 *     .union(upperTier)
 *     .subtract(spawnPlatform)
 *     .intersect(mapBounds)
 *     .build();
 * }</pre>
 * <p>
 *
 * @since 0.0.13
 */
public final class Area implements Containable {
    public enum Operation {
        UNION,
        SUBTRACT,
        INTERSECT
    }

    public record AreaEntry(
      Containable containable,
      Operation operation
    ) {}

    private final ImmutableList<AreaEntry> entries;

    private Area(List<AreaEntry> entries) {
        if (entries.isEmpty()) throw new IllegalArgumentException("Area must have at least one entry");
        this.entries = ImmutableList.copyOf(entries);
    }

    /**
     * Create an empty area.
     */
    public static Area empty() {
        return new Area(List.of());
    }

    /**
     * Begin building an Area, seeding it with an initial containable via UNION.
     */
    public static Builder from(Containable initial) {
        return new Builder(initial);
    }

    /**
     * Begin building an Area from a collection of containables, unioning them together.
     */
    public static Builder from(Collection<? extends Containable> containables) {
        if (containables.isEmpty()) throw new IllegalArgumentException("Must provide at least one containable");
        Builder builder = new Builder(containables.iterator().next());
        containables.stream().skip(1).forEach(builder::union);
        return builder;
    }


    public Set<AreaEntry> entries() {
        return Set.copyOf(entries);
    }


    /**
     * Evaluates the operation chain left to right.
     * Starts from {@code false} (empty set) and applies each operation in order.
     */
    @Override
    public boolean contains(Locatable locatable) {
        if (locatable == null) return false;

        boolean result = false;

        for (AreaEntry entry : entries) {
            boolean inside = entry.containable().contains(locatable);
            result = switch (entry.operation()) {
                case UNION -> result || inside;
                case SUBTRACT -> result && !inside;
                case INTERSECT -> result && inside;
            };
        }

        return result;
    }

    @Override
    public World world() {
        return entries.getFirst().containable().world();
    }

    @Override
    public BlockPos center() {
        // It makes more sense for the center to be the midpoint of the bounding box rather than the midpoint of the centers of the individual containables!
        return Locations.getMidpoint(List.of(min(), max()));
    }

    @Override
    public BlockPos min() {
        return Locations.getMinLocation(entries.stream().map(e -> e.containable.min()).toList());
    }

    @Override
    public BlockPos max() {
        return Locations.getMaxLocation(entries.stream().map(e -> e.containable.max()).toList());
    }

    public static final class Builder implements Buildable<Area> {
        private final List<AreaEntry> entries = new ArrayList<>();

        private Builder(Containable initial) {
            entries.add(new AreaEntry(initial, Operation.UNION));
        }

        private void validateContainable(Containable containable) {
            if (containable == null) throw new IllegalArgumentException("Containable cannot be null");
            if (!containable.world().equals(entries.getFirst().containable().world())) throw new IllegalArgumentException("All containables must be in the same world");
        }

        public Builder union(Containable containable) {
            validateContainable(containable);
            entries.add(new AreaEntry(containable, Operation.UNION));
            return this;
        }

        public Builder subtract(Containable containable) {
            validateContainable(containable);
            entries.add(new AreaEntry(containable, Operation.SUBTRACT));
            return this;
        }

        public Builder intersect(Containable containable) {
            validateContainable(containable);
            entries.add(new AreaEntry(containable, Operation.INTERSECT));
            return this;
        }

        @Override
        public Area build() {
            return new Area(new ArrayList<>(entries));
        }

    }

}