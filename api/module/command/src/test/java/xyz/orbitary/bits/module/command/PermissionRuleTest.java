/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */
package xyz.orbitary.bits.module.command;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionRuleTest {

    private static final String DEFAULT = "bits.command.ban";

    private static PermissionClause any(String... nodes) {
        return new PermissionClause(List.of(nodes), PermissionType.ANY);
    }

    private static PermissionClause all(String... nodes) {
        return new PermissionClause(List.of(nodes), PermissionType.ALL);
    }

    private static boolean allows(PermissionRule rule, String... held) {
        return rule.allows(Set.of(held)::contains);
    }

    @Test
    void noClausesNeedsTheDefaultNode() {
        PermissionRule rule = new PermissionRule(DEFAULT, List.of());

        assertTrue(allows(rule, DEFAULT));
        assertFalse(allows(rule, "other"));
    }

    @Test
    void anyClauseAddsAnAlternativeToTheDefaultNode() {
        PermissionRule rule = new PermissionRule(DEFAULT, List.of(any("biomebattle.dev")));

        assertTrue(allows(rule, DEFAULT));
        assertTrue(allows(rule, "biomebattle.dev"));
        assertFalse(allows(rule, "other"));
    }

    @Test
    void allClauseIsAndedWithTheDefaultOrAnyClause() {
        PermissionRule rule = new PermissionRule(DEFAULT, List.of(any("biomebattle.dev"), all("this.permission")));

        assertTrue(allows(rule, "biomebattle.dev", "this.permission"));
        assertTrue(allows(rule, DEFAULT, "this.permission"));
        assertFalse(allows(rule, "biomebattle.dev"));
        assertFalse(allows(rule, "this.permission"));
    }

    @Test
    void allOnlyStillNeedsTheDefaultNode() {
        PermissionRule rule = new PermissionRule(DEFAULT, List.of(all("x")));

        assertFalse(allows(rule, "x"));
        assertTrue(allows(rule, DEFAULT, "x"));
    }

    @Test
    void defaultNodeIsOnlyAnAlternativeInTheFirstAnyClause() {
        PermissionRule rule = new PermissionRule(DEFAULT, List.of(any("a"), any("b")));

        assertFalse(allows(rule, DEFAULT));
        assertTrue(allows(rule, "a", "b"));
    }

}
