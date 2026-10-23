/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.module.command.Parameter;
import xyz.orbitary.bits.wrapper.GreedyString;
import xyz.orbitary.bits.wrapper.Link;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrigadierGroupsAndAliasesTest {

    private static final List<String> calls = new ArrayList<>();
    private static final Object SOURCE = new Object();

    @BeforeEach
    void clear() {
        calls.clear();
    }

    @Command("player")
    public static final class PlayerGroup {
        private final String target;

        public PlayerGroup(@Parameter("target") String target) {
            this.target = target;
        }

        @Command("")
        public void root() {
            calls.add("root " + target);
        }

        @Command("say")
        public void say(String message) {
            calls.add(target + " says " + message);
        }
    }

    @Command("outer")
    public static final class Outer {
        public Outer(@Parameter("a") String a) {}

        @Command("mid")
        public static final class Mid {
            private final int b;

            public Mid(@Parameter("b") Integer b) {
                this.b = b;
            }

            @Command("leaf")
            public void leaf() {
                calls.add("leaf " + b);
            }
        }
    }

    @Command("teleport")
    @SuppressWarnings("unused")
    public static final class Teleport {
        @Command(value = "here", aliases = {"h"})
        public void here() {
            calls.add("here");
        }
    }

    @SuppressWarnings("unused")
    public static final class Ping {
        @Command(value = "ping", aliases = {"p", "pong"})
        public void ping() {
            calls.add("ping");
        }
    }

    @SuppressWarnings("unused")
    public static final class Open {
        @Command("open")
        public void open(Link link) {
            calls.add(link.value);
        }
    }

    @SuppressWarnings("unused")
    public static final class GreedyGroup {
        @Command("g")
        public static final class Inner {
            public Inner(@Parameter("message") GreedyString message) {}

            @Command("x")
            public void x() {}
        }
    }

    private static CommandDispatcher<Object> dispatcher(Class<?> owner) {
        BrigadierCommandBuilder<Object> builder = new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(),
          ignored -> null,
          (source, error) -> {},
          (source, node) -> true, Runnable::run
        );
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        List<CommandDefinition> commands = CommandScanner.scan(owner);
        builder.build(commands).forEach(dispatcher::register);
        return dispatcher;
    }

    private static List<String> completions(CommandDispatcher<Object> dispatcher, String input) throws Exception {
        return dispatcher.getCompletionSuggestions(dispatcher.parse(input, SOURCE)).get().getList().stream()
          .map(suggestion -> suggestion.getText())
          .sorted()
          .toList();
    }

    @Test
    void groupArgumentFeedsTheGroupInstanceBeforeItsSubcommand() throws CommandSyntaxException {
        dispatcher(PlayerGroup.class).execute("player bob say hi", SOURCE);

        assertEquals(List.of("bob says hi"), calls);
    }

    @Test
    void groupArgumentIsRequiredBeforeTheSubcommand() {
        CommandDispatcher<Object> dispatcher = dispatcher(PlayerGroup.class);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("player say hi", SOURCE));
        assertEquals(List.of(), calls);
    }

    @Test
    void groupHandlerRunsWithTheGroupArgument() throws CommandSyntaxException {
        dispatcher(PlayerGroup.class).execute("player bob", SOURCE);

        assertEquals(List.of("root bob"), calls);
    }

    @Test
    void nestedGroupsReadOnlyTheArgumentsOfTheGroupTheyCreate() throws CommandSyntaxException {
        dispatcher(Outer.class).execute("outer x mid 2 leaf", SOURCE);

        assertEquals(List.of("leaf 2"), calls);
    }

    @Test
    void subcommandsCompleteAfterTheGroupArgument() throws Exception {
        assertEquals(List.of("say"), completions(dispatcher(PlayerGroup.class), "player bob "));
    }

    @Test
    void groupWithAGreedyArgumentIsRejected() {
        List<CommandDefinition> commands = CommandScanner.scan(GreedyGroup.class);

        assertThrows(IllegalArgumentException.class, () -> new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(), ignored -> null, (source, error) -> {}, (source, node) -> true, Runnable::run).build(commands));
    }

    @Test
    void commandAliasRunsTheSameCommand() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(Teleport.class);

        dispatcher.execute("teleport here", SOURCE);
        dispatcher.execute("teleport h", SOURCE);

        assertEquals(List.of("here", "here"), calls);
    }

    @Test
    void methodAliasesRunTheSameCommand() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(Ping.class);

        dispatcher.execute("ping", SOURCE);
        dispatcher.execute("p", SOURCE);
        dispatcher.execute("pong", SOURCE);

        assertEquals(List.of("ping", "ping", "ping"), calls);
    }

    @Test
    void urlArgumentParsesAndCompletesSchemes() throws Exception {
        CommandDispatcher<Object> dispatcher = dispatcher(Open.class);

        dispatcher.execute("open \"https://example.com\"", SOURCE);

        assertEquals(List.of("https://example.com"), calls);
        assertEquals(List.of("http://", "https://", "www."), completions(dispatcher, "open "));
    }

}
