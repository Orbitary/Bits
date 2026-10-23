/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

/**
 * How the nodes of a {@link Permission} combine.
 *
 * @since 0.1.6
 */
public enum PermissionType {

    /**
     * Any one of the nodes grants access.
     *
     * @since 0.1.6
     */
    ANY,

    /**
     * Every node is required.
     *
     * @since 0.1.6
     */
    ALL

}
