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
 * Wrapper commonly used for builders - which returns the set on add()
 *
 * @param <E> the type of the set
 */
public class AddableSet<E> implements Buildable<Set<E>> {
    private final Set<E> set = new HashSet<>();

    @Override
    public final Set<E> build() {
        return Set.copyOf(set);
    }

    public final AddableSet<E> add(E element) {
        set.add(element);
        return this;
    }

    @SafeVarargs
    public final AddableSet<E> addAll(E... element) {
        for (E e : element) {
            add(e);
        }
        return this;
    }

    public final AddableSet<E> addAll(Collection<? extends E> element) {
        for (E e : element) {
            add(e);
        }
        return this;
    }


    public final Collector<E, ?, AddableSet<E>> toCollector() {
        return Collector.of(
          AddableSet::empty,
          AddableSet::add,
          (left, right) -> left.addAll(right.build())
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
