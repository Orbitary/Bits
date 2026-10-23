/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import xyz.orbitary.bits.log.BasicLogger;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.module.command.Condition;
import xyz.orbitary.bits.module.command.Guard;
import xyz.orbitary.bits.module.command.Parameter;
import xyz.orbitary.bits.wrapper.GreedyString;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Behaviour of the builder beyond the basic tree shape: return codes, error handling, conditions and argument parsing.
 */
class BrigadierCommandBehaviourTest {

    private static final List<String> calls = new ArrayList<>();
    private static final List<Object> sources = new ArrayList<>();
    private static final Object SOURCE = new Object();

    @BeforeAll
    static void logger() {
        new BasicLogger(LoggerFactory.getLogger("test"), Logger.LogFlags.defaultFlags());
    }

    @BeforeEach
    void clear() {
        calls.clear();
        sources.clear();
    }

    @SuppressWarnings("unused")
    public static final class Optionals {
        @Command("opt")
        public void opt(@Parameter(required = false) String first, @Parameter(required = false) Integer second) {
            calls.add(first + " " + second);
        }
    }

    @SuppressWarnings("unused")
    public static final class Numbers {
        @Command("num")
        public void num(Long whole, Double decimal, Boolean flag) {
            calls.add(whole + " " + decimal + " " + flag);
        }
    }

    @Command("outer")
    @SuppressWarnings("unused")
    public static final class Outer {
        @Command("mid")
        public static final class Mid {
            @Command("leaf")
            public void leaf() {
                calls.add("leaf");
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Returns {
        @Command("ok")
        public void ok() {
            calls.add("ok");
        }
    }

    @SuppressWarnings("unused")
    public static final class Throws {
        @Command("boom")
        public void boom() {
            throw new IllegalStateException("boom");
        }
    }

    public static final class AllowAll implements Condition<Object> {
        @Override
        public boolean test(Object actor) {
            return true;
        }
    }

    public static final class DenyAll implements Condition<Object> {
        @Override
        public boolean test(Object actor) {
            return false;
        }
    }

    public static final class RecordSource implements Condition<Object> {
        @Override
        public boolean test(Object actor) {
            sources.add(actor);
            return true;
        }
    }

    @SuppressWarnings("unused")
    public static final class Allowed {
        @Command("allowed")
        @Guard(AllowAll.class)
        public void allowed() {
            calls.add("allowed");
        }
    }

    @SuppressWarnings("unused")
    public static final class Recorded {
        @Command("recorded")
        @Guard(RecordSource.class)
        public void recorded() {
            calls.add("recorded");
        }
    }

    @Command("locked")
    @Guard(DenyAll.class)
    @SuppressWarnings("unused")
    public static final class Locked {
        @Command("x")
        public void x() {
            calls.add("x");
        }
    }

    @SuppressWarnings("unused")
    public static final class Say {
        @Command("say")
        public void say(String target, GreedyString message) {
            calls.add(target + ": " + message);
        }
    }

    @SuppressWarnings("unused")
    public static final class OptionalSay {
        @Command("optsay")
        public void optsay(@Parameter(required = false) GreedyString message) {
            calls.add(String.valueOf(message));
        }
    }

    @SuppressWarnings("unused")
    public static final class GreedyNotLast {
        @Command("bad")
        public void bad(GreedyString message, String target) {}
    }

    @Test
    void greedyStringAsLastArgumentTakesTheRestOfTheInput() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Say.class), builder(new ArrayList<>()));

        dispatcher.execute("say bob hello there  world", SOURCE);

        assertEquals(List.of("bob: hello there  world"), calls);
    }

    @Test
    void optionalGreedyStringCanBeOmitted() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(OptionalSay.class), builder(new ArrayList<>()));

        dispatcher.execute("optsay", SOURCE);
        dispatcher.execute("optsay a b", SOURCE);

        assertEquals(List.of("null", "a b"), calls);
    }

    @Test
    void greedyStringNotLastIsRejectedAtBuild() {
        List<CommandDefinition> commands = CommandScanner.scan(GreedyNotLast.class);

        assertThrows(IllegalArgumentException.class, () -> builder(new ArrayList<>()).build(commands));
    }

    @Test
    void optionalGreedyStringCanBeOmittedInOrder() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Optionals.class), builder(new ArrayList<>()));

        dispatcher.execute("opt", SOURCE);
        dispatcher.execute("opt x", SOURCE);
        dispatcher.execute("opt x 2", SOURCE);

        assertEquals(List.of("null null", "x null", "x 2"), calls);
    }

    @Test
    void numericAndBooleanArgumentsParse() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Numbers.class), builder(new ArrayList<>()));

        dispatcher.execute("num 5 2.5 true", SOURCE);

        assertEquals(List.of("5 2.5 true"), calls);
    }

    @Test
    void commandsInThreeLevelsRun() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Outer.class), builder(new ArrayList<>()));

        dispatcher.execute("outer mid leaf", SOURCE);

        assertEquals(List.of("leaf"), calls);
    }

    @Test
    void successfulCommandReturnsOne() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Returns.class), builder(new ArrayList<>()));

        assertEquals(1, dispatcher.execute("ok", SOURCE));
    }

    @Test
    void thrownExceptionsReachTheErrorHandlerAndReturnZero() throws CommandSyntaxException {
        List<Throwable> errors = new ArrayList<>();
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Throws.class), builder(errors));

        assertEquals(0, dispatcher.execute("boom", SOURCE));
        assertEquals(1, errors.size());
        assertInstanceOf(IllegalStateException.class, errors.getFirst());
    }

    @Test
    void passingConditionRunsTheCommand() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Allowed.class), builder(new ArrayList<>()));

        dispatcher.execute("allowed", SOURCE);

        assertEquals(List.of("allowed"), calls);
    }

    @Test
    void conditionReceivesTheCommandSource() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Recorded.class), builder(new ArrayList<>()));

        dispatcher.execute("recorded", SOURCE);

        assertEquals(SOURCE, sources.getFirst());
        assertEquals(List.of("recorded"), calls);
    }

    @Test
    void failingGroupConditionBlocksEverySubcommand() throws CommandSyntaxException {
        List<Throwable> errors = new ArrayList<>();
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Locked.class), builder(errors));

        assertEquals(0, dispatcher.execute("locked x", SOURCE));
        assertEquals(List.of(), calls);
        assertInstanceOf(ConditionFailedException.class, errors.getFirst());
    }

    @Test
    void errorHandlerReceivesTheSourceOfTheFailedCommand() throws CommandSyntaxException {
        List<Object> failedSources = new ArrayList<>();
        BrigadierCommandBuilder<Object> builder = new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(),
          ignored -> null,
          (source, error) -> failedSources.add(source),
          (source, node) -> true, Runnable::run
        );
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Throws.class), builder);

        dispatcher.execute("boom", SOURCE);

        assertEquals(SOURCE, failedSources.getFirst());
    }

    @Test
    void missingRequiredArgumentIsNotExecutableAtTheLiteral() {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Numbers.class), builder(new ArrayList<>()));

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("num 5", SOURCE));
    }

    private static BrigadierCommandBuilder<Object> builder(List<Throwable> errors) {
        return new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(),
          ignored -> null,
          (source, error) -> errors.add(error),
          (source, node) -> true, Runnable::run
        );
    }

    private static CommandDispatcher<Object> dispatcher(List<CommandDefinition> commands, BrigadierCommandBuilder<Object> builder) {
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        builder.build(commands).forEach(dispatcher::register);
        return dispatcher;
    }

}
