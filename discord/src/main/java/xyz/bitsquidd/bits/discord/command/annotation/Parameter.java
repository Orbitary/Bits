/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides Discord option metadata for a command method parameter.
 * <p>
 * If omitted, the framework falls back to the reflection parameter name
 * (requires {@code -parameters} compiler flag) or the type simple name.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    String value();
    String description() default "No description provided.";
    boolean required() default true;
}
