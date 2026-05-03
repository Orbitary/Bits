/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.annotation;

import xyz.bitsquidd.bits.mc.command.requirement.BitsCommandRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies extra command requirements to be checked before executing the command.
 * <p>
 * Note: Classes are used to define a requirement, instances of said class should be registered.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Requirement {
    Class<? extends BitsCommandRequirement>[] value();

}