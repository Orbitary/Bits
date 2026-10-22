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
    public static final class Flat {
        @Command(value = "ban", description = "Bans a user")
        public void ban(@Parameter(value = "Target User", description = "Who to ban") String target, @Parameter(required = false) Integer days) {}

        @Command("ping")
        public void ping() {}

        @Command("untagged")
        public void untagged(String first, long second) {}

        private void hidden() {}
    }

    @Command(value = "admin", description = "Server administration")
    @Async
    @Guard(DenyAll.class)
    public static final class Admin {
        @Command("")
        public void root() {}

        @Command(value = "kick", description = "Kicks a user")
        public void kick(String target) {}

        @Command("sub")
        @Guard(AllowAll.class)
        public void sub() {}

        @Command(value = "group", description = "Nested group")
        public static final class Group {
            @Command("deep")
            public void deep(String value) {}
        }
    }

    @SuppressWarnings("unused")
    public static final class Injected {
        @Command("say")
        public void say(Context context, String message) {}
    }

    public static final class Context {}

    public static final class DenyAll implements Condition<Object> {
        @Override
        public boolean test(Object actor) {
            return false;
        }
    }

    public static final class AllowAll implements Condition<Object> {
        @Override
        public boolean test(Object actor) {
            return true;
        }
    }

    @SuppressWarnings("unused")
    public static final class OptionalThenRequired {
        @Command("bad")
        public void bad(@Parameter(required = false) String first, String second) {}
    }

    @SuppressWarnings("unused")
    public static final class BlankMethodOutsideGroup {
        @Command("")
        public void blank() {}
    }

    @Command("")
    @SuppressWarnings("unused")
    public static final class BlankGroup {
        @Command("x")
        public void x() {}
    }

    @SuppressWarnings("unused")
    public static final class DuplicatePath {
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
        CommandDefinition ban = find(Flat.class, "ban");

        assertEquals("ban", ban.path().get(0).name());
        assertEquals("Bans a user", ban.path().get(0).description());
        assertEquals(Flat.class, ban.owner());
    }

    @Test
    void commandWithoutDescriptionHasEmptyDescription() {
        assertEquals("", find(Flat.class, "ping").path().get(0).description());
    }

    @Test
    void normalisesArgumentNames() {
        List<ParameterDefinition> parameters = find(Flat.class, "ban").parameters();

        assertEquals("target_user", parameters.get(0).name());
        assertEquals("Who to ban", parameters.get(0).description());
        assertEquals(String.class, parameters.get(0).type());
    }

    @Test
    void parameterDefaultsToRequired() {
        List<ParameterDefinition> parameters = find(Flat.class, "ban").parameters();

        assertTrue(parameters.get(0).required());
        assertFalse(parameters.get(1).required());
        assertEquals(Integer.class, parameters.get(1).type());
    }

    @Test
    void unannotatedParameterDoesNotFail() {
        // Regression: a missing @Parameter used to throw a NullPointerException during dispatch.
        List<ParameterDefinition> parameters = find(Flat.class, "untagged").parameters();

        assertEquals(2, parameters.size());
        assertEquals("", parameters.get(0).description());
        assertTrue(parameters.get(0).required());
        assertEquals(long.class, parameters.get(1).type());
    }

    @Test
    void onlyCommandMethodsAreScanned() {
        assertEquals(3, CommandScanner.scan(Flat.class).size());
    }

    @Test
    void classWithoutCommandsYieldsNothing() {
        assertEquals(List.of(), CommandScanner.scan(String.class));
    }

    @Test
    void groupPrefixesSubcommandPaths() {
        CommandDefinition kick = find(Admin.class, "admin kick");

        assertEquals(List.of("admin", "kick"), names(kick));
        assertEquals("Server administration", kick.path().get(0).description());
        assertEquals("Kicks a user", kick.path().get(1).description());
    }

    @Test
    void blankMethodNameIsGroupHandler() {
        CommandDefinition root = find(Admin.class, "admin");

        assertEquals(1, root.path().size());
        assertEquals("Server administration", root.path().get(0).description());
    }

    @Test
    void nestedGroupsAddSegments() {
        assertEquals(List.of("admin", "group", "deep"), names(find(Admin.class, "admin group deep")));
    }

    @Test
    void asyncInheritsFromGroup() {
        assertTrue(find(Admin.class, "admin kick").async());
        assertFalse(find(Flat.class, "ping").async());
    }

    @Test
    void methodGuardReplacesGroupGuard() {
        assertEquals(List.of(AllowAll.class), find(Admin.class, "admin sub").conditions());
        assertEquals(List.of(DenyAll.class), find(Admin.class, "admin kick").conditions());
    }

    @Test
    void injectedParametersAreLeftOut() {
        CommandDefinition say = CommandScanner.scan(Injected.class, Context.class::equals).get(0);

        assertEquals(1, say.parameters().size());
        assertEquals(String.class, say.parameters().get(0).type());
        assertEquals(1, say.parameters().get(0).index());
    }

    @Test
    void rejectsRequiredAfterOptional() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(OptionalThenRequired.class));
    }

    @Test
    void rejectsBlankNameOutsideGroup() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(BlankMethodOutsideGroup.class));
    }

    @Test
    void rejectsBlankGroupName() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(BlankGroup.class));
    }

    @Test
    void rejectsDuplicatePaths() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(DuplicatePath.class));
    }

    @Test
    void rejectsNonPublicCommands() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(NonPublic.class));
    }

    @Test
    void rejectsDuplicateArgumentNamesAfterNormalising() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(DuplicateArgument.class));
    }

    private static List<String> names(CommandDefinition command) {
        return command.path().stream().map(CommandDefinition.Segment::name).toList();
    }

    private static CommandDefinition find(Class<?> owner, String path) {
        return CommandScanner.scan(owner).stream()
          .filter(command -> String.join(" ", names(command)).equals(path))
          .findFirst()
          .orElseThrow();
    }

}
