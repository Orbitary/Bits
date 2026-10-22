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
 * Marks a public method as a command. The method's parameters become the command's arguments.
 *
 * @since 0.1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /**
     * @return the command name
     *
     * @since 0.1.6
     */
    String value();

    /**
     * @return a description shown to users, or empty for none
     *
     * @since 0.1.6
     */
    String description() default "";

}
