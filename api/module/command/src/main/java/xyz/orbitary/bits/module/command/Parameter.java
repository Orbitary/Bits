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
 * Customises a command method parameter.
 *
 * @since 0.1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {

    /**
     * @return the argument name, or empty to use the parameter's own name
     *
     * @since 0.1.6
     */
    String value() default "";

    /**
     * @return a description shown to users, or empty for none
     *
     * @since 0.1.6
     */
    String description() default "";

    /**
     * @return whether the argument must be supplied
     *
     * @since 0.1.6
     */
    boolean required() default true;

}
