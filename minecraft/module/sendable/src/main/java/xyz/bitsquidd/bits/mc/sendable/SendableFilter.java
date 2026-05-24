/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable;


import xyz.bitsquidd.bits.mc.sendable.impl.Sendable;
import xyz.bitsquidd.bits.mc.sendable.impl.SendableHandle;

import java.util.function.Predicate;


@FunctionalInterface
public interface SendableFilter<S extends Sendable> extends Predicate<SendableHandle<? extends S>> {

    static <S extends Sendable> SendableFilter<S> ofClass(Class<? extends S> clazz) {
        return handle -> clazz.isAssignableFrom(handle.definition().getClass());
    }

    
    static <S extends Sendable> SendableFilter<S> alwaysTrue() {
        return h -> true;
    }

    static <S extends Sendable> SendableFilter<S> alwaysFalse() {
        return h -> false;
    }

}
