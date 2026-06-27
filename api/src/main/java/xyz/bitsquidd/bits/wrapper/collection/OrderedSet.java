/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.wrapper.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;


/**
 * A simple implementation of an ordered set that maintains insertion order while ensuring element uniqueness.
 * <p>
 * This class combines the properties of an {@link ArrayList} for ordering and a {@link HashSet} for
 * fast uniqueness checks.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @since 0.0.10
 */
public final class OrderedSet<E> implements Iterable<E> {
    private final ArrayList<E> list = new ArrayList<>();
    private final HashSet<E> set = new HashSet<>();

    @Override
    public String toString() {
        return "OrderedSet{" + list + '}';
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof OrderedSet<?> that && set.equals(that.set));
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    /**
     * Returns a sequential {@link Stream} with this set as its source.
     *
     * @return a stream of the elements in this set
     *
     * @since 0.0.10
     */
    public Stream<E> stream() {
        return list.stream();
    }


    /**
     * Adds the specified element to this set if it is not already present.
     * <p>
     * If the set did not already contain the element, it is appended to the end of the insertion order.
     *
     * @param element the element to be added
     *
     * @return true if the element was added, false if it was already present
     *
     * @since 0.0.10
     */
    public boolean add(E element) {
        if (set.add(element)) {
            list.add(element);
            return true;
        }
        return false;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param element the element to be removed
     *
     * @return true if the set contained the specified element
     *
     * @since 0.0.10
     */
    public boolean remove(E element) {
        if (set.remove(element)) {
            list.remove(element);
            return true;
        }
        return false;
    }

    /**
     * Removes all elements from this set.
     *
     * @return always true
     *
     * @since 0.0.10
     */
    public boolean clear() {
        list.clear();
        set.clear();
        return true;
    }

    /**
     * Checks if this set contains any elements.
     *
     * @return true if the set is empty, false otherwise
     *
     * @since 0.0.10
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Checks if the specified element is present in this set.
     *
     * @param element the element to check for
     *
     * @return true if the element is present, false otherwise
     *
     * @since 0.0.10
     */
    public boolean contains(E element) {
        return set.contains(element);
    }

    /**
     * Returns the element at the specified position in the insertion order.
     *
     * @param index the index of the element to return
     *
     * @return the element at the specified index
     *
     * @throws IndexOutOfBoundsException if the index is out of range
     * @since 0.0.10
     */
    public E get(int index) {
        return list.get(index);
    }


    /**
     * Returns the total number of unique elements in this set.
     *
     * @return the size of the set
     *
     * @since 0.0.10
     */
    public int size() {
        return list.size();
    }

}