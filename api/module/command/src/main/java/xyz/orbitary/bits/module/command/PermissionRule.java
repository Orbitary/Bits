/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.module.command;

import java.util.List;
import java.util.function.Predicate;

/**
 * The permission requirements of one command. The default node is the command's own node, such as
 * {@code bits.command.admin.ban}.
 * <p>
 * The command runs when every clause passes. The default node is an alternative in the first {@link PermissionType#ANY}
 * clause, and the command needs it on its own when there is no {@code ANY} clause.
 *
 * @param defaultNode the command's own node
 * @param clauses     the requirements, class-level first
 *
 * @since 0.1.6
 */
public record PermissionRule(String defaultNode, List<PermissionClause> clauses) {

    /**
     * @param has returns true if the actor holds a permission node
     *
     * @return true if the actor may run the command
     *
     * @since 0.1.6
     */
    public boolean allows(Predicate<String> has) {
        boolean anyClause = clauses.stream().anyMatch(clause -> clause.type() == PermissionType.ANY);
        if (!anyClause && !has.test(defaultNode)) return false;

        boolean firstAny = true;
        for (PermissionClause clause : clauses) {
            boolean passed;
            if (clause.type() == PermissionType.ALL) {
                passed = clause.nodes().stream().allMatch(has);
            } else {
                passed = firstAny && has.test(defaultNode);
                firstAny = false;
                passed = passed || clause.nodes().stream().anyMatch(has);
            }
            if (!passed) return false;
        }
        return true;
    }

}
