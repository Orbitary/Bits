/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.event;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import xyz.orbitary.bits.log.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * A node in an event graph.
 *
 * @since 0.1.6
 */
public final class EventNode<T extends Event> {
    private static final AtomicLong NEXT_ID = new AtomicLong();
    private static final BiConsumer<Event, Throwable> DEFAULT_ERROR_HANDLER = (e, ex) -> Logger.exception("Event listener failed for " + e.getClass().getName(), ex);

    private final Object lock = new Object();
    private final long id = NEXT_ID.getAndIncrement();

    private final String name;
    private final Class<T> type;
    private final @Nullable Predicate<Event> gate;
    private final CopyOnWriteArrayList<Registration<?>> registrations = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<EventNode<?>> children = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<Class<?>, Dispatcher<?>> dispatchers = new ConcurrentHashMap<>();

    private volatile @Nullable EventNode<?> parent;
    private volatile @Nullable BiConsumer<? super Event, ? super Throwable> errorHandler;

    private EventNode(final String name, final Class<T> type, final @Nullable Predicate<? super T> filter) {
        this.name = name;
        this.type = type;
        this.gate = filter == null ? null : event -> filter.test(type.cast(event));
    }


    //region Construction

    /**
     * @param name a non-blank name
     *
     * @return a node that accepts every event
     *
     * @since 0.1.6
     */
    public static EventNode<Event> all(final String name) {
        return of(name, Event.class, null);
    }

    /**
     * @param name a non-blank name
     * @param type the events this node and its descendants receive; other events skip the whole subtree
     * @param <T>  the event type
     *
     * @return a node limited to the event type
     *
     * @since 0.1.6
     */
    public static <T extends Event> EventNode<T> of(final String name, final Class<T> type) {
        return of(name, type, null);
    }

    /**
     * @param name   a non-blank name
     * @param type   the events this node and its descendants receive; other events skip the whole subtree
     * @param filter a condition checked for each listener in the subtree, or null for none. Events failing it skip the
     *               whole subtree. A listener runs only if every filter on its path to the dispatching node passes
     * @param <T>    the event type
     *
     * @return a node limited to the event type and filter
     *
     * @since 0.1.6
     */
    public static <T extends Event> EventNode<T> of(final String name, final Class<T> type, final @Nullable Predicate<? super T> filter) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        if (name.isBlank()) throw new IllegalArgumentException("Event node name cannot be blank");
        return new EventNode<>(name, type, filter);
    }
    //endregion


    //region Tree

    /**
     * @return the node's name
     *
     * @since 0.1.6
     */
    public String name() {
        return name;
    }

    /**
     * @return the parent node, or null for a root
     *
     * @since 0.1.6
     */
    public @Nullable EventNode<?> parent() {
        return parent;
    }

    /**
     * @return an immutable snapshot of the direct children, in the order they were added
     *
     * @since 0.1.6
     */
    public @Unmodifiable List<EventNode<?>> children() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Adds a child. Its listeners run after this node's own listeners of the same priority.
     *
     * @param child a root node with a name not used by a sibling
     *
     * @return this node
     *
     * @throws IllegalArgumentException if the child is this node, an ancestor of it, or its name is used by a sibling
     * @throws IllegalStateException    if the child already has a parent
     * @since 0.1.6
     */
    public EventNode<T> addChild(final EventNode<? extends T> child) {
        Objects.requireNonNull(child, "child");
        if (child == this) throw new IllegalArgumentException("A node cannot be its own child: " + name);

        boolean added = false;
        while (!added) {
            final EventNode<?> root = root();
            added = lockBoth(
              root, child, () -> {
                  if (root() != root) return false;
                  if (child.parent != null) throw new IllegalStateException("Node already has a parent: " + child.name);

                  for (EventNode<?> ancestor = this; ancestor != null; ancestor = ancestor.parent) {
                      if (ancestor == child) throw new IllegalArgumentException("Adding " + child.name + " under " + name + " would create a cycle");
                  }

                  for (final EventNode<?> sibling : children) {
                      if (sibling.name.equals(child.name)) throw new IllegalArgumentException("Duplicate child name under " + name + ": " + child.name);
                  }

                  child.parent = this;
                  children.add(child);
                  invalidate(null);
                  return true;
              }
            );
        }
        return this;
    }

    /**
     * @param child a node
     *
     * @return true if the node was a direct child and is now detached
     *
     * @since 0.1.6
     */
    public boolean removeChild(final EventNode<? extends T> child) {
        Objects.requireNonNull(child, "child");
        return underRoot(
          () -> {
              if (child.parent != this || !children.remove(child)) return false;
              child.parent = null;
              invalidate(null);
              return true;
          }
        );
    }

    /**
     * Detaches every descendant with a name. A match takes its own subtree with it.
     *
     * @param name a node name
     *
     * @return how many nodes were detached
     *
     * @since 0.1.6
     */
    public int removeChildren(final String name) {
        Objects.requireNonNull(name, "name");
        return underRoot(() -> detachNamed(name));
    }

    private int detachNamed(final String wanted) {
        int removed = 0;
        boolean changed = false;
        for (final EventNode<?> child : children) {
            if (child.name.equals(wanted)) {
                child.parent = null;
                children.remove(child);
                removed++;
                changed = true;
            } else {
                removed += child.detachNamed(wanted);
            }
        }
        if (changed) invalidate(null);
        return removed;
    }

    /**
     * @param name a node name
     *
     * @return every descendant with that name
     *
     * @since 0.1.6
     */
    public List<EventNode<?>> findChildren(final String name) {
        final var found = new ArrayList<EventNode<?>>();
        collect(name, found);
        return found;
    }

    private void collect(final String wanted, final List<EventNode<?>> found) {
        for (final EventNode<?> child : children) {
            if (child.name.equals(wanted)) found.add(child);
            child.collect(wanted, found);
        }
    }

    /**
     * @return a human-readable dump of this subtree with listener counts
     *
     * @since 0.1.6
     */
    public String dump() {
        final var out = new StringBuilder();
        dump(out, 0);
        return out.toString();
    }

    private void dump(final StringBuilder out, final int depth) {
        out.repeat("  ", depth).append(name)
          .append(" [type=").append(type.getSimpleName())
          .append(", listeners=").append(registrations.size()).append("]\n");
        for (final EventNode<?> child : children) {
            child.dump(out, depth + 1);
        }
    }

    @Override
    public String toString() {
        return "EventNode[" + name + "]";
    }
    //endregion


    //region Listeners

    /**
     * @param type the event type, receiving that type and all its subtypes
     * @param <E>  the event type
     *
     * @return a builder for a listener with a priority, filters or an expiry
     *
     * @since 0.1.6
     */
    public <E extends T> ListenerBuilder<E> listener(final Class<E> type) {
        return new ListenerBuilder<>(Objects.requireNonNull(type, "type"));
    }

    /**
     * Registers a listener with priority 0 that skips cancelled events.
     *
     * @param type    the event type
     * @param handler the work to do. To deny the event, cancel it
     * @param <E>     the event type
     *
     * @return a handle to remove the listener
     *
     * @since 0.1.6
     */
    public <E extends T> EventSubscription handle(final Class<E> type, final Consumer<? super E> handler) {
        return listener(type).handle(handler);
    }

    /**
     * Sets where errors thrown by this node's listeners are reported. Nodes without one use their parent's, and the
     * default logs them. An event whose listener fails is cancelled.
     *
     * @param handler receives the event and the error
     *
     * @return this node
     *
     * @since 0.1.6
     */
    public EventNode<T> onError(final BiConsumer<? super Event, ? super Throwable> handler) {
        this.errorHandler = Objects.requireNonNull(handler, "handler");
        return this;
    }

    private BiConsumer<? super Event, ? super Throwable> resolveErrorHandler() {
        for (EventNode<?> node = this; node != null; node = node.parent) {
            final var handler = node.errorHandler;
            if (handler != null) return handler;
        }
        return DEFAULT_ERROR_HANDLER;
    }

    /**
     * Builds a listener with optional conditions.
     *
     * @param <E> the event type
     *
     * @since 0.1.6
     */
    public final class ListenerBuilder<E extends T> {
        private final Class<E> type;
        private final List<Predicate<? super E>> filters = new ArrayList<>();
        private @Nullable Predicate<? super E> expireWhen;
        private int expireCount = 0;
        private int priority = 0;
        private boolean ignoreCancelled = true;

        private ListenerBuilder(final Class<E> type) {
            this.type = type;
        }

        /**
         * Sets where the listener runs among every listener in the tree. The default is 0.
         *
         * @param priority lower runs first
         *
         * @return this builder
         *
         * @since 0.1.6
         */
        public ListenerBuilder<E> priority(final int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Sets whether the listener is skipped for events that are already cancelled. The default is true.
         *
         * @param ignoreCancelled false to also run for cancelled events
         *
         * @return this builder
         *
         * @since 0.1.6
         */
        public ListenerBuilder<E> ignoreCancelled(final boolean ignoreCancelled) {
            this.ignoreCancelled = ignoreCancelled;
            return this;
        }

        /**
         * Adds a condition the event must pass. All conditions must pass for the listener to run.
         *
         * @param filter the condition
         *
         * @return this builder
         *
         * @since 0.1.6
         */
        public ListenerBuilder<E> filter(final Predicate<? super E> filter) {
            filters.add(Objects.requireNonNull(filter, "filter"));
            return this;
        }

        /**
         * Removes the listener after it has run this many times.
         *
         * @param count a positive number of runs
         *
         * @return this builder
         *
         * @throws IllegalArgumentException if the count is not positive
         * @since 0.1.6
         */
        public ListenerBuilder<E> expireCount(final int count) {
            if (count <= 0) throw new IllegalArgumentException("expireCount must be positive: " + count);
            this.expireCount = count;
            return this;
        }

        /**
         * Removes the listener, without running it, the first time an event matches.
         *
         * @param predicate the expiry condition
         *
         * @return this builder
         *
         * @since 0.1.6
         */
        public ListenerBuilder<E> expireWhen(final Predicate<? super E> predicate) {
            this.expireWhen = Objects.requireNonNull(predicate, "predicate");
            return this;
        }

        /**
         * Registers the listener.
         *
         * @param handler the work to do. To deny the event, cancel it
         *
         * @return a handle to remove the listener
         *
         * @since 0.1.6
         */
        public EventSubscription handle(final Consumer<? super E> handler) {
            Objects.requireNonNull(handler, "handler");

            @Nullable Predicate<E> filter = null;
            for (final Predicate<? super E> next : filters) {
                filter = filter == null ? next::test : filter.and(next);
            }

            final var registration = new Registration<>(
              EventNode.this, type, priority, ignoreCancelled, filter, expireWhen,
              expireCount > 0 ? new AtomicInteger(expireCount) : null, handler
            );

            locked(
              () -> {
                  registrations.add(registration);
                  invalidate(type);
              }
            );
            return registration;
        }

    }
    //endregion


    //region Dispatch

    /**
     * Dispatches an event to this node's listeners and its descendants.
     *
     * @param event the event
     * @param <E>   the event type
     *
     * @return the same event, so a caller can check whether it was cancelled
     *
     * @since 0.1.6
     */
    public <E extends T> E call(final E event) {
        Objects.requireNonNull(event, "event");
        dispatcherFor(event.getClass()).dispatch(event);
        return event;
    }

    /**
     * @param type the exact event type
     * @param <E>  the event type
     *
     * @return a cached dispatcher for the type
     *
     * @since 0.1.6
     */
    public <E extends T> EventDispatcher<E> dispatcher(final Class<E> type) {
        Objects.requireNonNull(type, "type");
        @SuppressWarnings("unchecked") final Dispatcher<E> dispatcher = (Dispatcher<E>)dispatcherFor(type);
        return dispatcher;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Dispatcher<?> dispatcherFor(final Class<?> type) {
        return dispatchers.computeIfAbsent(type, key -> new Dispatcher(key));
    }

    /**
     * Drops cached plans on this node and every ancestor. Must hold the root lock.
     *
     * @param changed the listener type that changed, so only plans that can include it are dropped. Null drops all.
     */
    private void invalidate(final @Nullable Class<?> changed) {
        for (final Dispatcher<?> dispatcher : dispatchers.values()) {
            if (changed == null || changed.isAssignableFrom(dispatcher.type)) dispatcher.plan = null;
        }
        final EventNode<?> p = parent;
        if (p != null) p.invalidate(changed);
    }

    private EventNode<?> root() {
        EventNode<?> node = this;
        for (EventNode<?> p = node.parent; p != null; p = node.parent) {
            node = p;
        }
        return node;
    }

    private <R> R underRoot(final Supplier<R> action) {
        while (true) {
            final EventNode<?> root = root();
            synchronized (root.lock) {
                if (root() == root) return action.get();
            }
        }
    }

    private void locked(final Runnable action) {
        underRoot(
          () -> {
              action.run();
              return Boolean.TRUE;
          }
        );
    }

    private static boolean lockBoth(final EventNode<?> a, final EventNode<?> b, final Supplier<Boolean> action) {
        if (a == b) {
            synchronized (a.lock) {
                return action.get();
            }
        }

        final EventNode<?> first = a.id < b.id ? a : b;
        final EventNode<?> second = first == a ? b : a;

        synchronized (first.lock) {
            synchronized (second.lock) {
                return action.get();
            }
        }
    }

    private Plan compile(final Class<?> eventType) {
        final List<Entry> entries = new ArrayList<>();

        for (final Registration<?> registration : registrations) {
            if (registration.type.isAssignableFrom(eventType)) entries.add(new Entry(registration, gate));
        }

        for (final EventNode<?> child : children) {
            if (!child.type.isAssignableFrom(eventType)) continue;
            for (final Entry entry : child.dispatcherFor(eventType).plan().entries) {
                entries.add(entry.under(gate));
            }
        }

        entries.sort(Comparator.comparingInt(entry -> entry.registration.priority));
        return new Plan(entries.toArray(new Entry[0]));
    }

    private record Plan(Entry[] entries) {
        boolean isEmpty() {
            return entries.length == 0;
        }

    }

    /**
     * A listener with the node filters between it and the node the plan belongs to, outermost first.
     */
    private record Entry(
      Registration<?> registration,
      @Nullable Predicate<Event> gate
    ) {
        Entry under(final @Nullable Predicate<Event> outer) {
            if (outer == null) return this;
            return new Entry(registration, gate == null ? outer : outer.and(gate));
        }

    }

    private final class Dispatcher<E extends Event> implements EventDispatcher<E> {
        private final Class<E> type;
        private volatile @Nullable Plan plan;

        private Dispatcher(final Class<E> type) {
            this.type = type;
        }

        private Plan plan() {
            final Plan cached = plan;
            if (cached != null) return cached;
            return underRoot(
              () -> {
                  Plan current = plan;
                  if (current == null) {
                      current = compile(type);
                      plan = current;
                  }
                  return current;
              }
            );
        }

        private void dispatch(final Event event) {
            for (final Entry entry : plan().entries) {
                entry.registration.run(event, entry.gate);
            }
        }

        @Override
        public E call(final E event) {
            Objects.requireNonNull(event, "event");
            if (event.getClass() != type) {
                throw new IllegalArgumentException("Dispatcher is for " + type.getName() + " but got " + event.getClass().getName());
            }
            dispatch(event);
            return event;
        }

        @Override
        public boolean hasListener() {
            return !plan().isEmpty();
        }

    }

    private static final class Registration<E extends Event> implements EventSubscription {
        private final EventNode<?> node;
        private final Class<? extends E> type;
        private final int priority;
        private final boolean ignoreCancelled;
        private final @Nullable Predicate<? super E> filter;
        private final @Nullable Predicate<? super E> expireWhen;
        private final @Nullable AtomicInteger remaining;
        private final Consumer<? super E> body;
        private volatile boolean active = true;

        private Registration(
          final EventNode<?> node, final Class<? extends E> type, final int priority, final boolean ignoreCancelled,
          final @Nullable Predicate<? super E> filter, final @Nullable Predicate<? super E> expireWhen,
          final @Nullable AtomicInteger remaining, final Consumer<? super E> body
        ) {
            this.node = node;
            this.type = type;
            this.priority = priority;
            this.ignoreCancelled = ignoreCancelled;
            this.filter = filter;
            this.expireWhen = expireWhen;
            this.remaining = remaining;
            this.body = body;
        }

        private void run(final Event raw, final @Nullable Predicate<? super Event> gate) {
            if (!active) return;
            try {
                if (gate != null && !gate.test(raw)) return;
                if (ignoreCancelled && raw.cancelled()) return;

                final E event = type.cast(raw);
                if (expireWhen != null && expireWhen.test(event)) {
                    unsubscribe();
                    return;
                }
                if (filter != null && !filter.test(event)) return;

                boolean last = false;
                if (remaining != null) {
                    final int left = remaining.decrementAndGet();
                    if (left < 0) {
                        unsubscribe();
                        return;
                    }
                    last = left == 0;
                }

                body.accept(event);
                if (last) unsubscribe();
            } catch (final Throwable error) {
                try {
                    node.resolveErrorHandler().accept(raw, error);
                } catch (final Throwable ignored) {}
                raw.cancel("Listener failed: " + error);
            }
        }

        @Override
        public void unsubscribe() {
            if (!active) return;
            node.locked(
              () -> {
                  if (!active) return;
                  active = false;
                  node.registrations.remove(this);
                  node.invalidate(type);
              }
            );
        }

        @Override
        public boolean active() {
            return active;
        }

    }
    //endregion

}
