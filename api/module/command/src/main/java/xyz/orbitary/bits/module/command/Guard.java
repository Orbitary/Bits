/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Attaches a {@link Condition} that must pass before a command runs. A method guard replaces the class guard.
 *
 * @since 0.1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Guard {

    /**
     * @return the condition class, instantiated by the platform adapter
     *
     * @since 0.1.6
     */
    Class<? extends Condition<?>> value();

}
