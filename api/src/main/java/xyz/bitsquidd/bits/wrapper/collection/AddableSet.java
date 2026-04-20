/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;


/**
 * Wrapper - commonly used for builders - which returns the set on add()
 *
 * @param <T> the type of the set
 */
public class AddableSet<T> implements Buildable<Set<T>> {
    private final Set<T> set = new HashSet<>();

    @Override
    public final Set<T> build() {
        return set;
    }

    public final AddableSet<T> add(T element) {
        set.add(element);
        return this;
    }

    @SafeVarargs
    public final AddableSet<T> addAll(T... element) {
        for (T e : element) {
            add(e);
        }
        return this;
    }

    public final AddableSet<T> addAll(Collection<? extends T> element) {
        for (T e : element) {
            add(e);
        }
        return this;
    }


    public final Set<T> toSet() {
        return Set.copyOf(set);
    }

    public final Collector<T, ?, AddableSet<T>> toCollector() {
        return Collector.of(
          AddableSet::empty,
          AddableSet::add,
          (left, right) -> left.addAll(right.toSet())
        );
    }


    @SafeVarargs
    public static <T> AddableSet<T> of(T... element) {
        return new AddableSet<T>().addAll(element);
    }

    public static <T> AddableSet<T> of(Collection<? extends T> set) {
        return new AddableSet<T>().addAll(set);
    }


    public static <T> AddableSet<T> empty() {
        return new AddableSet<>();
    }


}
