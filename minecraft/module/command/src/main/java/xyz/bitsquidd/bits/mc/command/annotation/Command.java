/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class or method as a command.
 * <p>
 * When applied to a class, it creates a command group. When applied to a method,
 * it creates an executable command.
 * <p>
 * Example usage:
 * <pre>{@code
 * @Command(value = "give", description = "Give an item")
 * public class GiveCommand extends BitsCommand { ... }
 * }</pre>
 *
 * @since 0.0.10
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String value() default "";

    String[] aliases() default {};

    String description() default "";

}