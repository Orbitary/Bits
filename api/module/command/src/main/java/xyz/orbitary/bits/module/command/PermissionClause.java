/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.util.List;

/**
 * One {@link Permission} annotation, read from a command.
 *
 * @param nodes the permission nodes
 * @param type  whether any one node or every node is required
 *
 * @since 0.1.6
 */
public record PermissionClause(List<String> nodes, PermissionType type) {}
