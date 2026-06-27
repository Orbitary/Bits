/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.lifecycle.builder;

import java.util.function.Supplier;


/**
 * A functional interface for objects that can be built into a final instance.
 *
 * @param <B> the type of object that this builder produces
 *
 * @since 0.0.10
 */
public interface Buildable<B> extends Supplier<B> {
    /**
     * Constructs and returns the final object instance.
     *
     * @return the built object
     *
     * @since 0.0.10
     */
    B build();


    @Override
    default B get() {
        return build();
    }

}
