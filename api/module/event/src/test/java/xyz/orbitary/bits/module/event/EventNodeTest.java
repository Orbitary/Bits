/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventNodeTest {
    abstract static class Named extends Event {
        private final int id;

        Named(final int id) {
            this.id = id;
        }

        int id() {
            return id;
        }
    }

    static final class Ping extends Named {
        Ping(final int id) {
            super(id);
        }
    }

    static final class Pong extends Named {
        Pong(final int id) {
            super(id);
        }
    }

    static final class Unrelated extends Event {}

    private EventNode<Event> root;
    private List<String> log;

    @BeforeEach
    void setUp() {
        root = EventNode.all("root");
        log = Collections.synchronizedList(new ArrayList<>());
    }

    private EventNode<Event> child(final EventNode<Event> parent, final String name) {
        final var node = EventNode.all(name);
        parent.addChild(node);
        return node;
    }

    //region Dispatch
    @Test
    void exactTypeListenerReceivesEvent() {
        root.handle(Ping.class, e -> log.add("ping" + e.id()));
        root.call(new Ping(1));
        root.call(new Pong(2));
        assertEquals(List.of("ping1"), log);
    }

    @Test
    void supertypeListenerReceivesSubtypes() {
        root.handle(Named.class, e -> log.add("named"));
        root.handle(Event.class, e -> log.add("event"));
        root.call(new Ping(1));
        root.call(new Unrelated());
        assertEquals(List.of("named", "event", "event"), log);
    }

    @Test
    void callReturnsTheSameEvent() {
        final var ping = new Ping(1);
        assertSame(ping, root.call(ping));
    }

    @Test
    void registrationOrderIsKeptAtEqualPriority() {
        root.handle(Ping.class, e -> log.add("a"));
        root.handle(Ping.class, e -> log.add("b"));
        root.handle(Ping.class, e -> log.add("c"));
        root.call(new Ping(1));
        assertEquals(List.of("a", "b", "c"), log);
    }

    @Test
    void lowerPriorityRunsFirstRegardlessOfRegistrationOrder() {
        root.listener(Ping.class).priority(10).handle(e -> log.add("late"));
        root.handle(Ping.class, e -> log.add("default"));
        root.listener(Ping.class).priority(-10).handle(e -> log.add("early"));
        root.call(new Ping(1));
        assertEquals(List.of("early", "default", "late"), log);
    }

    @Test
    void priorityOrdersListenersAcrossTheWholeTree() {
        final var a = child(root, "a");
        final var b = child(root, "b");
        root.listener(Ping.class).priority(5).handle(e -> log.add("root5"));
        a.listener(Ping.class).priority(10).handle(e -> log.add("a10"));
        b.listener(Ping.class).priority(-1).handle(e -> log.add("b-1"));
        a.listener(Ping.class).priority(0).handle(e -> log.add("a0"));
        root.call(new Ping(1));
        assertEquals(List.of("b-1", "a0", "root5", "a10"), log);
    }

    @Test
    void unhandledEventIsLeftAlone() {
        final var order = root.call(new Ping(1));
        assertFalse(order.cancelled());
    }
    //endregion

    //region Cancellation
    @Test
    void cancelSkipsLaterListenersByDefault() {
        root.listener(Ping.class).priority(-1).handle(e -> e.cancel("no"));
        root.handle(Ping.class, e -> log.add("handler"));

        final var order = root.call(new Ping(1));

        assertTrue(order.cancelled());
        assertEquals("no", order.cancelReason());
        assertEquals(List.of(), log);
    }

    @Test
    void listenerCanOptOutOfSkippingCancelledEvents() {
        root.listener(Ping.class).priority(-1).handle(e -> e.cancel("no"));
        root.listener(Ping.class).priority(1).ignoreCancelled(false).handle(e -> log.add("saw:" + e.cancelReason()));
        root.call(new Ping(1));
        assertEquals(List.of("saw:no"), log);
    }

    @Test
    void listenersBeforeTheCancelStillRun() {
        root.handle(Ping.class, e -> log.add("early"));
        root.listener(Ping.class).priority(1).handle(e -> e.cancel("no"));
        root.call(new Ping(1));
        assertEquals(List.of("early"), log);
    }

    @Test
    void cancellingKeepsTheFirstReason() {
        final var order = new Ping(1);
        order.cancel("first");
        order.cancel("second");
        assertEquals("first", order.cancelReason());
    }

    @Test
    void skippedCancelledListenerDoesNotConsumeExpiry() {
        root.listener(Ping.class).priority(-1).filter(e -> e.id() == 1).handle(e -> e.cancel("no"));
        root.listener(Ping.class).expireCount(1).handle(e -> log.add("order" + e.id()));
        root.call(new Ping(1));
        root.call(new Ping(2));
        assertEquals(List.of("order2"), log);
    }

    //endregion

    //region Tree
    @Test
    void eventsFlowDownNeverUp() {
        final var kid = child(root, "kid");
        root.handle(Ping.class, e -> log.add("root"));
        kid.handle(Ping.class, e -> log.add("kid"));

        root.call(new Ping(1));
        assertEquals(List.of("root", "kid"), log);

        log.clear();
        kid.call(new Ping(2));
        assertEquals(List.of("kid"), log);
    }

    @Test
    void equalPriorityRunsOwnListenersThenChildrenInInsertionOrder() {
        final var first = child(root, "first");
        final var second = child(root, "second");
        second.handle(Ping.class, e -> log.add("second"));
        first.handle(Ping.class, e -> log.add("first"));
        root.handle(Ping.class, e -> log.add("root"));
        root.call(new Ping(1));
        assertEquals(List.of("root", "first", "second"), log);
    }

    @Test
    void listenerAddedDeepInTheTreeInvalidatesCachedAncestorPlans() {
        final var mid = child(root, "mid");
        final var leaf = child(mid, "leaf");
        final EventDispatcher<Ping> dispatcher = root.dispatcher(Ping.class);

        assertFalse(dispatcher.hasListener());
        dispatcher.call(new Ping(1));

        leaf.handle(Ping.class, e -> log.add("leaf"));

        assertTrue(dispatcher.hasListener());
        dispatcher.call(new Ping(2));
        assertEquals(List.of("leaf"), log);
    }

    @Test
    void listenerAddedForASupertypeInvalidatesSubtypePlans() {
        final EventDispatcher<Ping> dispatcher = root.dispatcher(Ping.class);
        dispatcher.call(new Ping(1));
        root.handle(Named.class, e -> log.add("named"));
        dispatcher.call(new Ping(2));
        assertEquals(List.of("named"), log);
    }

    @Test
    void addedChildIsPickedUpByCachedPlans() {
        final EventDispatcher<Ping> dispatcher = root.dispatcher(Ping.class);
        dispatcher.call(new Ping(1));

        final var kid = EventNode.all("kid");
        kid.handle(Ping.class, e -> log.add("kid"));
        root.addChild(kid);

        dispatcher.call(new Ping(2));
        assertEquals(List.of("kid"), log);
    }

    @Test
    void removedChildStopsReceivingAndIsDetached() {
        final var kid = child(root, "kid");
        kid.handle(Ping.class, e -> log.add("kid"));
        final EventDispatcher<Ping> dispatcher = root.dispatcher(Ping.class);
        dispatcher.call(new Ping(1));
        log.clear();

        assertTrue(root.removeChild(kid));
        assertNull(kid.parent());
        dispatcher.call(new Ping(2));
        assertEquals(List.of(), log);

        kid.call(new Ping(3));
        assertEquals(List.of("kid"), log);
    }

    @Test
    void removingANonChildReturnsFalse() {
        final var stranger = EventNode.all("stranger");
        final var kid = child(root, "kid");
        final var grandchild = child(kid, "grandchild");
        assertFalse(root.removeChild(stranger));
        assertFalse(root.removeChild(grandchild));
    }

    @Test
    void childCanBeReattachedAfterRemoval() {
        final var kid = child(root, "kid");
        root.removeChild(kid);
        final var other = EventNode.all("other");
        other.addChild(kid);
        assertSame(other, kid.parent());
    }

    @Test
    void treeRejectsInvalidStructure() {
        final var mid = child(root, "mid");
        final var leaf = child(mid, "leaf");

        assertThrows(IllegalArgumentException.class, () -> root.addChild(root));
        assertThrows(IllegalArgumentException.class, () -> leaf.addChild(root), "cycle through ancestors");
        assertThrows(IllegalStateException.class, () -> root.addChild(leaf), "already has a parent");
        assertThrows(IllegalArgumentException.class, () -> root.addChild(EventNode.all("mid")), "duplicate sibling name");
        assertThrows(IllegalArgumentException.class, () -> EventNode.all(" "));
    }

    @Test
    void sameNameIsAllowedUnderDifferentParents() {
        final var a = child(root, "a");
        final var b = child(root, "b");
        child(a, "same");
        child(b, "same");
        assertEquals(2, root.findChildren("same").size());
    }

    @Test
    void findChildrenSearchesRecursively() {
        final var a = child(root, "a");
        final var deep = child(a, "deep");
        assertEquals(List.of(deep), root.findChildren("deep"));
        assertEquals(List.of(), root.findChildren("missing"));
        assertEquals(List.of(), root.findChildren("root"));
    }

    @Test
    void dumpListsTheWholeSubtree() {
        final var a = child(root, "a");
        child(a, "deep");
        a.handle(Ping.class, e -> {});
        final String dump = root.dump();
        assertTrue(dump.contains("root [type=Event, listeners=0]"), dump);
        assertTrue(dump.contains("  a [type=Event, listeners=1]"), dump);
        assertTrue(dump.contains("    deep [type=Event, listeners=0]"), dump);
    }
    //endregion

    //region Listener features
    @Test
    void unsubscribeStopsTheListenerAndIsIdempotent() {
        final EventSubscription subscription = root.handle(Ping.class, e -> log.add("x"));
        assertTrue(subscription.active());
        root.call(new Ping(1));

        subscription.unsubscribe();
        subscription.unsubscribe();

        assertFalse(subscription.active());
        root.call(new Ping(2));
        assertEquals(List.of("x"), log);
    }

    @Test
    void expireCountRunsExactlyThatManyTimes() {
        final EventSubscription subscription = root.listener(Ping.class).expireCount(2).handle(e -> log.add("x"));
        for (int i = 0; i < 5; i++) root.call(new Ping(i));
        assertEquals(List.of("x", "x"), log);
        assertFalse(subscription.active());
        assertFalse(root.dispatcher(Ping.class).hasListener());
    }

    @Test
    void expireWhenRemovesWithoutRunning() {
        final EventSubscription subscription = root.listener(Ping.class)
          .expireWhen(e -> e.id() == 2)
          .handle(e -> log.add("id" + e.id()));
        root.call(new Ping(1));
        root.call(new Ping(2));
        root.call(new Ping(3));
        assertEquals(List.of("id1"), log);
        assertFalse(subscription.active());
    }

    @Test
    void filtersMustAllPassAndDoNotConsumeExpiry() {
        root.listener(Ping.class)
          .filter(e -> e.id() > 0)
          .filter(e -> e.id() < 10)
          .expireCount(1)
          .handle(e -> log.add("id" + e.id()));
        root.call(new Ping(-1));
        root.call(new Ping(99));
        root.call(new Ping(5));
        root.call(new Ping(6));
        assertEquals(List.of("id5"), log);
    }

    @Test
    void expiringListenerStillCancelsOnItsLastRun() {
        root.listener(Ping.class).expireCount(1).handle(e -> e.cancel("once"));
        assertTrue(root.call(new Ping(1)).cancelled());
        assertFalse(root.call(new Ping(2)).cancelled());
    }

    @Test
    void invalidExpireCountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> root.listener(Ping.class).expireCount(0));
    }
    //endregion

    //region Node filters
    @Test
    void nodeTypeSkipsTheWholeSubtreeForOtherEvents() {
        final var pings = EventNode.of("pings", Ping.class);
        root.addChild(pings);
        pings.handle(Ping.class, e -> log.add("ping"));
        pings.addChild(EventNode.of("deep", Ping.class));
        root.call(new Ping(1));
        root.call(new Pong(2));
        assertEquals(List.of("ping"), log);
        assertFalse(root.dispatcher(Pong.class).hasListener());
    }

    @Test
    void nodeFilterGatesItsListenersAndDescendants() {
        final var evens = EventNode.of("evens", Ping.class, e -> e.id() % 2 == 0);
        root.addChild(evens);
        final var inner = EventNode.of("inner", Ping.class, e -> e.id() > 2);
        evens.addChild(inner);
        root.handle(Ping.class, e -> log.add("root" + e.id()));
        evens.handle(Ping.class, e -> log.add("evens" + e.id()));
        inner.handle(Ping.class, e -> log.add("inner" + e.id()));

        for (int i = 1; i <= 4; i++) root.call(new Ping(i));

        assertEquals(List.of("root1", "root2", "evens2", "root3", "root4", "evens4", "inner4"), log);
    }

    @Test
    void nodeFilterAppliesWhenDispatchingFromTheFilteredNode() {
        final var evens = EventNode.of("evens", Ping.class, e -> e.id() % 2 == 0);
        evens.handle(Ping.class, e -> log.add("evens" + e.id()));
        evens.call(new Ping(1));
        evens.call(new Ping(2));
        assertEquals(List.of("evens2"), log);
    }

    @Test
    void failingNodeFilterCancelsTheEvent() {
        final var guarded = EventNode.of("guarded", Ping.class, e -> {
            throw new IllegalStateException("filter boom");
        });
        root.addChild(guarded);
        guarded.handle(Ping.class, e -> {});
        assertTrue(root.call(new Ping(1)).cancelled());
    }

    @Test
    void invalidNodeArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> EventNode.of("", Ping.class));
    }
    //endregion

    //region Removal by name
    @Test
    void removeChildrenDetachesEveryMatchRecursively() {
        final var a = child(root, "a");
        final var b = child(root, "b");
        final var aKid = child(a, "kid");
        final var bKid = child(b, "kid");
        aKid.handle(Ping.class, e -> log.add("aKid"));
        bKid.handle(Ping.class, e -> log.add("bKid"));
        final EventDispatcher<Ping> dispatcher = root.dispatcher(Ping.class);
        dispatcher.call(new Ping(1));
        log.clear();

        assertEquals(2, root.removeChildren("kid"));

        assertNull(aKid.parent());
        assertNull(bKid.parent());
        assertEquals(List.of(), a.children());
        dispatcher.call(new Ping(2));
        assertEquals(List.of(), log);
        assertFalse(dispatcher.hasListener());
    }

    @Test
    void removeChildrenTakesTheMatchedSubtreeAndIgnoresMisses() {
        final var a = child(root, "a");
        child(a, "a");
        assertEquals(0, root.removeChildren("missing"));
        assertEquals(1, root.removeChildren("a"));
        assertEquals(List.of(), root.children());
    }
    //endregion

    //region Failure and dispatchers
    @Test
    void throwingListenerIsReportedAndCancelsTheEvent() {
        final List<Throwable> errors = new ArrayList<>();
        root.onError((event, error) -> errors.add(error));
        root.handle(Ping.class, e -> {
            throw new IllegalStateException("boom");
        });
        root.handle(Ping.class, e -> log.add("skipped"));
        root.listener(Ping.class).ignoreCancelled(false).handle(e -> log.add("still runs"));

        final var ping = root.call(new Ping(1));

        assertTrue(ping.cancelled());
        assertEquals(List.of("still runs"), log);
        assertEquals(1, errors.size());
        assertEquals("boom", errors.getFirst().getMessage());
    }

    @Test
    void throwingErrorHandlerIsContained() {
        root.onError((event, error) -> {
            throw new IllegalStateException("handler boom");
        });
        root.handle(Ping.class, e -> {
            throw new IllegalStateException("listener boom");
        });
        assertTrue(root.call(new Ping(1)).cancelled());
    }

    @Test
    void dispatcherOnlyAcceptsItsExactType() {
        final EventDispatcher<Named> dispatcher = root.dispatcher(Named.class);
        @SuppressWarnings({"unchecked", "rawtypes"}) final EventDispatcher<Event> raw = (EventDispatcher)dispatcher;
        assertThrows(IllegalArgumentException.class, () -> raw.call(new Ping(1)));
    }

    @Test
    void hasListenerSeesDescendantsAndSupertypes() {
        final var kid = child(root, "kid");
        assertFalse(root.dispatcher(Ping.class).hasListener());
        final EventSubscription subscription = kid.handle(Named.class, e -> {});
        assertTrue(root.dispatcher(Ping.class).hasListener());
        assertFalse(root.dispatcher(Unrelated.class).hasListener());
        subscription.unsubscribe();
        assertFalse(root.dispatcher(Ping.class).hasListener());
    }

    @Test
    void childErrorsGoToTheChildsOwnHandler() {
        final var kid = child(root, "kid");
        final AtomicInteger rootErrors = new AtomicInteger();
        final AtomicInteger kidErrors = new AtomicInteger();
        root.onError((event, error) -> rootErrors.incrementAndGet());
        kid.onError((event, error) -> kidErrors.incrementAndGet());
        kid.handle(Ping.class, e -> {
            throw new IllegalStateException();
        });
        root.call(new Ping(1));
        assertEquals(0, rootErrors.get());
        assertEquals(1, kidErrors.get());
    }

    @Test
    void childWithoutAHandlerInheritsItsParents() {
        final var mid = child(root, "mid");
        final var leaf = child(mid, "leaf");
        final AtomicInteger rootErrors = new AtomicInteger();
        root.onError((event, error) -> rootErrors.incrementAndGet());
        leaf.handle(Ping.class, e -> {
            throw new IllegalStateException();
        });
        root.call(new Ping(1));
        assertEquals(1, rootErrors.get());
    }
    //endregion

    //region Reentrancy and edge cases
    @Test
    void listenerCanUnsubscribeItselfDuringDispatch() {
        final EventSubscription[] self = new EventSubscription[1];
        self[0] = root.handle(Ping.class, e -> {
            log.add("once");
            self[0].unsubscribe();
        });
        root.call(new Ping(1));
        root.call(new Ping(2));
        assertEquals(List.of("once"), log);
    }

    @Test
    void listenerRegisteredDuringDispatchRunsFromTheNextEvent() {
        root.listener(Ping.class).expireCount(1).handle(e -> root.handle(Ping.class, later -> log.add("late" + later.id())));
        root.call(new Ping(1));
        root.call(new Ping(2));
        assertEquals(List.of("late2"), log);
    }

    @Test
    void listenerCanDispatchANestedEvent() {
        root.handle(Ping.class, e -> {
            log.add("ping" + e.id());
            if (e.id() == 1) root.call(new Pong(2));
        });
        root.handle(Pong.class, e -> log.add("pong" + e.id()));
        root.call(new Ping(1));
        assertEquals(List.of("ping1", "pong2"), log);
    }

    @Test
    void dispatcherIsCachedPerType() {
        assertSame(root.dispatcher(Ping.class), root.dispatcher(Ping.class));
    }

    @Test
    void expireWhenIsCheckedBeforeExpireCount() {
        final EventSubscription subscription = root.listener(Ping.class)
          .expireCount(5)
          .expireWhen(e -> e.id() == 3)
          .handle(e -> log.add("id" + e.id()));
        for (int i = 1; i <= 4; i++) root.call(new Ping(i));
        assertEquals(List.of("id1", "id2"), log);
        assertFalse(subscription.active());
    }

    @Test
    void priorityStaysStableAcrossPlanRebuilds() {
        final var a = child(root, "a");
        final var b = child(root, "b");
        a.handle(Ping.class, e -> log.add("a"));
        b.handle(Ping.class, e -> log.add("b"));
        root.call(new Ping(1));
        root.handle(Pong.class, e -> {}).unsubscribe();
        root.call(new Ping(2));
        assertEquals(List.of("a", "b", "a", "b"), log);
    }

    @Test
    void typedNodeAcceptsSubtypeListenersOnly() {
        final var named = EventNode.of("named", Named.class);
        root.addChild(named);
        named.handle(Ping.class, e -> log.add("ping"));
        named.handle(Named.class, e -> log.add("named"));
        root.call(new Ping(1));
        root.call(new Unrelated());
        assertEquals(List.of("ping", "named"), log);
    }

    @Test
    void cancelledEventStillReachesListenersThatOptOutInChildNodes() {
        final var audit = child(root, "audit");
        root.listener(Ping.class).priority(-1).handle(e -> e.cancel("denied"));
        audit.listener(Ping.class).ignoreCancelled(false).handle(e -> log.add("audit:" + e.cancelReason()));
        audit.handle(Ping.class, e -> log.add("skipped"));
        root.call(new Ping(1));
        assertEquals(List.of("audit:denied"), log);
    }
    //endregion


    //region Targeted invalidation and tree locks
    @Test
    void unsubscribingASupertypeListenerInvalidatesSubtypePlans() {
        final EventSubscription subscription = root.handle(Named.class, e -> log.add("named"));
        final EventDispatcher<Ping> dispatcher = root.dispatcher(Ping.class);
        dispatcher.call(new Ping(1));
        subscription.unsubscribe();
        dispatcher.call(new Ping(2));
        assertEquals(List.of("named"), log);
        assertFalse(dispatcher.hasListener());
    }

    @Test
    void changingAListenerLeavesUnrelatedTypesWorking() {
        root.handle(Pong.class, e -> log.add("pong"));
        final EventDispatcher<Pong> pongs = root.dispatcher(Pong.class);
        pongs.call(new Pong(1));
        root.handle(Ping.class, e -> {}).unsubscribe();
        pongs.call(new Pong(2));
        assertEquals(List.of("pong", "pong"), log);
    }

    @Test
    void nodeMovedToAnotherTreeDispatchesInTheNewTree() {
        final var other = EventNode.all("other");
        final var kid = child(root, "kid");
        kid.handle(Ping.class, e -> log.add("kid"));
        root.call(new Ping(1));

        root.removeChild(kid);
        other.addChild(kid);
        root.call(new Ping(2));
        other.call(new Ping(3));

        assertEquals(List.of("kid", "kid"), log);
    }

    @Test
    void joiningTreesInOppositeOrdersDoesNotDeadlock() throws Exception {
        final var a = EventNode.all("a");
        final var b = EventNode.all("b");
        final var start = new CountDownLatch(1);
        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final List<Future<?>> futures = new ArrayList<>();
        for (final boolean forward : new boolean[]{true, false}) {
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < 20_000; i++) {
                    final var parent = forward ? a : b;
                    final var kid = forward ? b : a;
                    try {
                        parent.addChild(kid);
                    } catch (final IllegalArgumentException | IllegalStateException expected) {
                        // The other thread won this round.
                    }
                    parent.removeChild(kid);
                }
                return null;
            }));
        }
        start.countDown();
        for (final Future<?> future : futures) future.get(60, TimeUnit.SECONDS);
        pool.shutdown();
    }
    //endregion


    //region Concurrency
    @Test
    void dispatchSurvivesConcurrentStructuralChanges() throws Exception {
        final int dispatchers = 4;
        final int rounds = 20_000;
        final var permanentHits = new AtomicInteger();
        root.handle(Ping.class, e -> permanentHits.incrementAndGet());
        root.onError((event, error) -> log.add("error:" + error));

        final var churning = child(root, "churn");
        final var start = new CountDownLatch(1);
        final ExecutorService pool = Executors.newFixedThreadPool(dispatchers + 2);
        final List<Future<?>> futures = new ArrayList<>();

        for (int d = 0; d < dispatchers; d++) {
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < rounds; i++) root.call(new Ping(i));
                return null;
            }));
        }
        futures.add(pool.submit(() -> {
            start.await();
            for (int i = 0; i < rounds / 10; i++) {
                final EventSubscription subscription = churning.handle(Ping.class, e -> {});
                subscription.unsubscribe();
            }
            return null;
        }));
        futures.add(pool.submit(() -> {
            start.await();
            for (int i = 0; i < rounds / 10; i++) {
                final var temporary = EventNode.all("temp");
                temporary.handle(Ping.class, e -> {});
                churning.addChild(temporary);
                churning.removeChild(temporary);
            }
            return null;
        }));

        start.countDown();
        for (final Future<?> future : futures) future.get(60, TimeUnit.SECONDS);
        pool.shutdown();

        assertEquals(dispatchers * rounds, permanentHits.get(), "permanent listener must see every dispatch exactly once");
        assertEquals(List.of(), log, "no listener should have thrown");
        assertEquals(0, churning.children().size());
    }

    @Test
    void expireCountHoldsUnderConcurrentDispatch() throws Exception {
        final var hits = new AtomicInteger();
        root.listener(Ping.class).expireCount(100).handle(e -> hits.incrementAndGet());

        final int threads = 8;
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        final var start = new CountDownLatch(1);
        final List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < 1_000; i++) root.call(new Ping(i));
                return null;
            }));
        }
        start.countDown();
        for (final Future<?> future : futures) future.get(60, TimeUnit.SECONDS);
        pool.shutdown();

        assertEquals(100, hits.get());
    }
    //endregion

}
