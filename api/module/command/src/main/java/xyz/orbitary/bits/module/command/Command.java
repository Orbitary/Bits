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
 * Marks a command. On a method it defines a command whose parameters become its arguments. On a class it
 * defines a command group, and the {@link Command} methods and nested classes inside it become its subcommands.
 * A method in a {@link Command} class with a blank name is the group's own handler.
 *
 * @since 0.1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Command {

    /**
     * @return the command or group name
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
