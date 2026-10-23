/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.arguments.StringArgumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.module.command.Parameter;
import xyz.orbitary.bits.wrapper.GreedyString;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Greedy strings take the rest of the input verbatim, so they need their own checks: spacing, quoting, position
 * and how they combine with other arguments.
 */
class BrigadierGreedyStringTest {

    private static final List<String> calls = new ArrayList<>();
    private static final Object SOURCE = new Object();

    @BeforeEach
    void clear() {
        calls.clear();
    }

    public record Point(double x, double y) {}

    @SuppressWarnings("unused")
    public static final class Say {
        @Command("say")
        public void say(String target, GreedyString message) {
            calls.add(target + "|" + message);
        }
    }

    @SuppressWarnings("unused")
    public static final class Lone {
        @Command("shout")
        public void shout(GreedyString message) {
            calls.add(message.toString());
        }
    }

    @Command("admin")
    @SuppressWarnings("unused")
    public static final class Admin {
        @Command("broadcast")
        public void broadcast(GreedyString message) {
            calls.add("broadcast " + message);
        }
    }

    @SuppressWarnings("unused")
    public static final class AfterComposite {
        @Command("pt")
        public void pt(Point point, GreedyString message) {
            calls.add(point.x() + " " + point.y() + " " + message);
        }
    }

    @SuppressWarnings("unused")
    public static final class OptionalTail {
        @Command("tail")
        public void tail(String target, @Parameter(required = false) GreedyString message) {
            calls.add(target + "|" + message);
        }
    }

    @SuppressWarnings("unused")
    public static final class TwoGreedy {
        @Command("two")
        public void two(@Parameter("first") GreedyString first, @Parameter("second") GreedyString second) {}
    }

    @SuppressWarnings("unused")
    public static final class GreedyThenOptional {
        @Command("gto")
        public void gto(GreedyString first, @Parameter(required = false) String second) {}
    }

    private static BrigadierCommandBuilder<Object> builder(BrigadierArguments<Object> arguments) {
        return new BrigadierCommandBuilder<>(arguments, ignored -> null, (source, error) -> {}, Runnable::run);
    }

    private static BrigadierArguments<Object> arguments() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        arguments.registerComposite(Point.class, new BrigadierComposite<>(
          List.of(new BrigadierComposite.Part("x", Double.class), new BrigadierComposite.Part("y", Double.class)),
          values -> new Point((Double) values[0], (Double) values[1])
        ));
        return arguments;
    }

    private static CommandDispatcher<Object> dispatcher(Class<?> owner) {
        return dispatcher(CommandScanner.scan(owner));
    }

    private static CommandDispatcher<Object> dispatcher(List<CommandDefinition> commands) {
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        builder(arguments()).build(commands).forEach(dispatcher::register);
        return dispatcher;
    }

    @Test
    void takesTheRestOfTheInputIncludingQuotesAndInnerSpacing() throws CommandSyntaxException {
        dispatcher(Say.class).execute("say bob \"hi  there\"", SOURCE);

        assertEquals(List.of("bob|\"hi  there\""), calls);
    }

    @Test
    void keepsTrailingSpacesInTheValue() throws CommandSyntaxException {
        dispatcher(Say.class).execute("say bob hi ", SOURCE);

        assertEquals(List.of("bob|hi "), calls);
    }

    @Test
    void wholeInputCanBeTheGreedyValue() throws CommandSyntaxException {
        dispatcher(Lone.class).execute("shout one two three", SOURCE);

        assertEquals(List.of("one two three"), calls);
    }

    @Test
    void emptyRequiredGreedyIsASyntaxError() {
        CommandDispatcher<Object> dispatcher = dispatcher(Say.class);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("say bob", SOURCE));
        assertEquals(List.of(), calls);
    }

    @Test
    void greedyInsideAGroupTakesTheRest() throws CommandSyntaxException {
        dispatcher(Admin.class).execute("admin broadcast maintenance at noon", SOURCE);

        assertEquals(List.of("broadcast maintenance at noon"), calls);
    }

    @Test
    void greedyAfterACompositeReadsBothParts() throws CommandSyntaxException {
        dispatcher(AfterComposite.class).execute("pt 1 2 hello world", SOURCE);

        assertEquals(List.of("1.0 2.0 hello world"), calls);
    }

    @Test
    void optionalGreedyAfterARequiredArgument() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(OptionalTail.class);

        dispatcher.execute("tail bob", SOURCE);
        dispatcher.execute("tail bob a b", SOURCE);

        assertEquals(List.of("bob|null", "bob|a b"), calls);
    }

    @Test
    void twoGreedyArgumentsAreRejected() {
        List<CommandDefinition> commands = CommandScanner.scan(TwoGreedy.class);

        assertThrows(IllegalArgumentException.class, () -> builder(arguments()).build(commands));
    }

    @Test
    void greedyFollowedByAnotherArgumentIsRejected() {
        List<CommandDefinition> commands = CommandScanner.scan(GreedyThenOptional.class);

        assertThrows(IllegalArgumentException.class, () -> builder(arguments()).build(commands));
    }

    @Test
    void greedyArgumentDoesNotParseWordsAsOtherTypes() throws CommandSyntaxException {
        dispatcher(Lone.class).execute("shout 42 true", SOURCE);

        assertEquals(List.of("42 true"), calls);
    }

}
