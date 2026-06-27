/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.lifecycle.builder;

/**
 * An abstract base for builders that allows for fluent method chaining in sub-classes.
 *
 * @param <B>    the type of object being built
 * @param <SELF> the type of the builder itself, used for fluent returns
 *
 * @since 0.0.10
 */
public abstract class ExtendableBuildable<B, SELF extends ExtendableBuildable<B, SELF>> implements Buildable<B> {
    /**
     * Returns the current builder instance cast to its specific implementation type.
     *
     * @return this builder instance
     *
     * @since 0.0.10
     */
    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF)this;
    }

}
