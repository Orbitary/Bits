/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection;

import xyz.bitsquidd.bits.lifecycle.builder.Buildable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;


/**
 * Wrapper commonly used for builders - which returns the list on add()
 *
 * @param <E> the type of the list
 */
public class AddableList<E> implements Buildable<List<E>> {
    private final List<E> list = new ArrayList<>();

    @Override
    public final List<E> build() {
        return List.copyOf(list);
    }

    public final AddableList<E> add(E element) {
        list.add(element);
        return this;
    }

    @SafeVarargs
    public final AddableList<E> addAll(E... element) {
        for (E e : element) {
            add(e);
        }
        return this;
    }

    public final AddableList<E> addAll(Collection<? extends E> element) {
        for (E e : element) {
            add(e);
        }
        return this;
    }

    public final Collector<E, ?, AddableList<E>> toCollector() {
        return Collector.of(
          AddableList::empty,
          AddableList::add,
          (left, right) -> left.addAll(right.build())
        );
    }


    @SafeVarargs
    public static <T> AddableList<T> of(T... element) {
        return new AddableList<T>().addAll(element);
    }

    public static <T> AddableList<T> of(Collection<? extends T> list) {
        return new AddableList<T>().addAll(list);
    }


    public static <T> AddableList<T> empty() {
        return new AddableList<>();
    }


}
