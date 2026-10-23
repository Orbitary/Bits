/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires permission nodes to run a command. Applies to the command's method and to the classes it is nested in.
 * Repeat it to add several requirements, which must all pass. The command's default node is part of the first
 * {@link PermissionType#ANY} requirement.
 *
 * @since 0.1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(Permissions.class)
public @interface Permission {

    /**
     * @return the permission nodes
     *
     * @since 0.1.6
     */
    String[] value();

    /**
     * @return whether any one node or every node is required
     *
     * @since 0.1.6
     */
    PermissionType type() default PermissionType.ANY;

}
