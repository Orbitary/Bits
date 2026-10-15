/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides a command parameter's default completions with literal suggestions.
 *
 * @since 0.1.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Suggest {
    /**
     * Returns the literal completions offered for this parameter.
     *
     * @return the suggestions, or an empty array to suppress completions
     *
     * @since 0.1.1
     */
    String[] value();
}
