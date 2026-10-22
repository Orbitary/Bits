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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandScannerTest {

    @SuppressWarnings("unused")
    public static final class Valid {
        @Command(value = "ban", description = "Bans a user")
        public void ban(@Parameter(value = "Target User", description = "Who to ban") String target, @Parameter(required = false) Integer days) {}

        @Command("ping")
        public void ping() {}

        @Command("untagged")
        public void untagged(String first, long second) {}

        private void hidden() {}
    }

    @SuppressWarnings("unused")
    public static final class OptionalThenRequired {
        @Command("bad")
        public void bad(@Parameter(required = false) String first, String second) {}
    }

    @SuppressWarnings("unused")
    public static final class BlankName {
        @Command(" ")
        public void blank() {}
    }

    @SuppressWarnings("unused")
    public static final class DuplicateName {
        @Command("same")
        public void first() {}

        @Command("same")
        public void second() {}
    }

    @SuppressWarnings("unused")
    public static final class NonPublic {
        @Command("secret")
        void secret() {}
    }

    @SuppressWarnings("unused")
    public static final class DuplicateArgument {
        @Command("twice")
        public void twice(@Parameter("x") String first, @Parameter("X") String second) {}
    }

    @Test
    void readsNameAndDescription() {
        CommandDefinition ban = find(Valid.class, "ban");

        assertEquals("Bans a user", ban.description());
        assertEquals(Valid.class, ban.owner());
        assertEquals("ban", ban.method().getName());
    }

    @Test
    void commandWithoutDescriptionHasEmptyDescription() {
        assertEquals("", find(Valid.class, "ping").description());
    }

    @Test
    void normalisesArgumentNames() {
        List<ParameterDefinition> parameters = find(Valid.class, "ban").parameters();

        assertEquals("target_user", parameters.get(0).name());
        assertEquals("Who to ban", parameters.get(0).description());
        assertEquals(String.class, parameters.get(0).type());
    }

    @Test
    void parameterDefaultsToRequired() {
        List<ParameterDefinition> parameters = find(Valid.class, "ban").parameters();

        assertTrue(parameters.get(0).required());
        assertFalse(parameters.get(1).required());
        assertEquals(Integer.class, parameters.get(1).type());
    }

    @Test
    void unannotatedParameterDoesNotFail() {
        // Regression: a missing @Parameter used to throw a NullPointerException during dispatch.
        List<ParameterDefinition> parameters = find(Valid.class, "untagged").parameters();

        assertEquals(2, parameters.size());
        assertEquals("", parameters.get(0).description());
        assertTrue(parameters.get(0).required());
        assertEquals(long.class, parameters.get(1).type());
    }

    @Test
    void onlyCommandMethodsAreScanned() {
        assertEquals(3, CommandScanner.scan(Valid.class).size());
    }

    @Test
    void classWithoutCommandsYieldsNothing() {
        assertEquals(List.of(), CommandScanner.scan(String.class));
    }

    @Test
    void rejectsRequiredAfterOptional() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(OptionalThenRequired.class));
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(BlankName.class));
    }

    @Test
    void rejectsDuplicateCommandNames() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(DuplicateName.class));
    }

    @Test
    void rejectsNonPublicCommands() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(NonPublic.class));
    }

    @Test
    void rejectsDuplicateArgumentNamesAfterNormalising() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(DuplicateArgument.class));
    }

    private static CommandDefinition find(Class<?> owner, String name) {
        return CommandScanner.scan(owner).stream()
          .filter(command -> command.name().equals(name))
          .findFirst()
          .orElseThrow();
    }

}
