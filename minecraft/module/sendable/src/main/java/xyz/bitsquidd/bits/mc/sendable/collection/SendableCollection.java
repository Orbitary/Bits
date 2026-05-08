/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.collection;

import com.google.errorprone.annotations.DoNotMock;
import org.jetbrains.annotations.Unmodifiable;

import xyz.bitsquidd.bits.mc.sendable.Receiver;
import xyz.bitsquidd.bits.mc.sendable.SendableFilter;
import xyz.bitsquidd.bits.mc.sendable.SendableManager;
import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.Collection;
import java.util.List;


/**
 * Representation of a collection of sendables of a specific type.
 * <p>
 * <b>Developer Note: All extending classes must implement some form of add() functionality.</b>
 *
 * @param <S> The type of sendable in the collection.
 *
 * @since 0.0.14
 */
@DoNotMock
public sealed abstract class SendableCollection<S extends Sendable> permits KeyedSendableCollection, ListSendableCollection, SingleSendableCollection {
    private boolean needsForceRender = false;

    protected SendableCollection() {}

    //region Collection Operations
    @SuppressWarnings("unchecked") // We cast to <S> for simplicity - especially for public-facing methods.
    public final Collection<SendableHandle<S>> get(SendableFilter<? super S> filter) {
        return (Collection<SendableHandle<S>>)(Collection<?>)getAll().stream().filter(filter).toList();
    }

    @Unmodifiable
    public abstract List<SendableHandle<? extends S>> getAll();


    public final void remove(SendableFilter<? super S> filter) {
        get(filter).forEach(handle -> {
            if (handle.isExpired()) handle.bits$markForExpire();
            removeInternal(handle);
        });
        needsForceRender = true; // We mark a final render on remove.
    }

    protected abstract void removeInternal(SendableHandle<? extends S> handle);
    //endregion


    public final void tick() {
        getAll().forEach(SendableHandle::bits$tick);
        remove(SendableHandle::isExpired);
    }

    public final boolean needsRender() {
        return getAll().stream().anyMatch(SendableHandle::needsRender) || needsForceRender;
    }

    public final void markRendered() {
        getAll().forEach(SendableHandle::bits$markRendered);
        needsForceRender = false;
    }


    protected final <SE extends S> SendableHandle<SE> createHandle(SE sendable, Receiver receiver) {
        return new SendableHandle<>(sendable, manager(), receiver);
    }

    protected abstract SendableManager<S, ?> manager();


    // Collections must override their toString.
    @Override
    public abstract String toString();

}