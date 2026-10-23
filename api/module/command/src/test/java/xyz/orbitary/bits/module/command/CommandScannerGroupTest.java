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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandScannerGroupTest {

    @Command(value = "player", aliases = {"p"})
    @SuppressWarnings("unused")
    public static final class PlayerGroup {
        private final String target;

        public PlayerGroup(@Parameter("target") String target) {
            this.target = target;
        }

        @Command("say")
        public void say(String message) {}
    }

    @Command("outer")
    @SuppressWarnings("unused")
    public static final class Outer {
        public Outer(@Parameter("a") String a) {}

        @Command("mid")
        public static final class Mid {
            public Mid(@Parameter("b") Integer b) {}

            @Command("leaf")
            public void leaf() {}
        }
    }

    @SuppressWarnings("unused")
    public static final class NotAGroup {
        public NotAGroup(String value) {}

        @Command("run")
        public void run() {}
    }

    @Command("optional")
    @SuppressWarnings("unused")
    public static final class OptionalGroup {
        public OptionalGroup(@Parameter(value = "x", required = false) String x) {}

        @Command("run")
        public void run() {}
    }

    @Command("clash")
    @SuppressWarnings("unused")
    public static final class ClashingGroup {
        public ClashingGroup(@Parameter("name") String name) {}

        @Command("run")
        public void run(@Parameter("name") String name) {}
    }

    private static CommandDefinition find(List<CommandDefinition> commands, String path) {
        return commands.stream().filter(command -> command.fullName().equals(path)).findFirst().orElseThrow();
    }

    @Test
    void groupConstructorArgumentsComeBeforeTheSubcommands() {
        CommandDefinition say = find(CommandScanner.scan(PlayerGroup.class), "player say");

        assertEquals(1, say.path().get(0).parameters().size());
        assertEquals("target", say.path().get(0).parameters().get(0).name());
        assertEquals(1, say.constructorParameters().size());
        assertEquals("target", say.constructorParameters().get(0).name());
        assertEquals(PlayerGroup.class.getDeclaredConstructors()[0], say.constructor());
    }

    @Test
    void groupAliasesAreKeptOnTheSegment() {
        CommandDefinition say = find(CommandScanner.scan(PlayerGroup.class), "player say");

        assertEquals(List.of("p"), say.path().get(0).aliases());
    }

    @Test
    void nestedGroupConstructorArgumentsStayWithTheirGroup() {
        CommandDefinition leaf = find(CommandScanner.scan(Outer.class), "outer mid leaf");

        assertEquals("a", leaf.path().get(0).parameters().get(0).name());
        assertEquals("b", leaf.path().get(1).parameters().get(0).name());
        assertEquals("b", leaf.constructorParameters().get(0).name());
    }

    @Test
    void plainClassWithConstructorArgumentsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(NotAGroup.class));
    }

    @Test
    void optionalGroupArgumentIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(OptionalGroup.class));
    }

    @Test
    void argumentNamesMustBeUniqueAlongThePath() {
        assertThrows(IllegalArgumentException.class, () -> CommandScanner.scan(ClashingGroup.class));
    }

    @Test
    void methodsWithoutGroupArgumentsHaveNoConstructorArguments() {
        CommandDefinition plain = find(CommandScanner.scan(Outer.class), "outer mid leaf");

        assertTrue(find(CommandScanner.scan(PlayerGroup.class), "player say").parameters().size() == 1);
        assertTrue(plain.parameters().isEmpty());
    }

}
