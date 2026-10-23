/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */
package xyz.orbitary.bits.module.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandScannerPermissionTest {

    @Permission("group.node")
    @SuppressWarnings("unused")
    public static final class Admin {
        @Command("ban")
        @Permission(value = {"x", "y"}, type = PermissionType.ALL)
        public void ban() {}

        @Command("Kick Out")
        public void kick() {}
    }

    @Test
    void defaultNodeIsBuiltFromThePath() {
        CommandDefinition ban = CommandScanner.scan(Admin.class).stream()
          .filter(command -> command.fullName().equals("ban"))
          .findFirst()
          .orElseThrow();

        assertEquals("bits.command.ban", ban.permissions().defaultNode());
    }

    @Test
    void classAndMethodPermissionsAreCollectedInOrder() {
        CommandDefinition ban = CommandScanner.scan(Admin.class).stream()
          .filter(command -> command.fullName().equals("ban"))
          .findFirst()
          .orElseThrow();

        assertEquals(
          List.of(
            new PermissionClause(List.of("group.node"), PermissionType.ANY),
            new PermissionClause(List.of("x", "y"), PermissionType.ALL)
          ),
          ban.permissions().clauses()
        );
    }

    @Test
    void spacesInNamesBecomeUnderscoresInTheDefaultNode() {
        CommandDefinition kick = CommandScanner.scan(Admin.class).stream()
          .filter(command -> command.fullName().equals("Kick Out"))
          .findFirst()
          .orElseThrow();

        assertEquals("bits.command.kick_out", kick.permissions().defaultNode());
    }

}
