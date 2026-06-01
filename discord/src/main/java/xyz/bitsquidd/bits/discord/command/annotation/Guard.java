/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.annotation;

import xyz.bitsquidd.bits.discord.command.JdaCommandGuard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Attaches a {@link JdaCommandGuard} to a command class. The guard's {@link JdaCommandGuard#check}
 * runs before argument resolution; returning {@code false} aborts execution.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Guard {
    Class<? extends JdaCommandGuard> value();
}
