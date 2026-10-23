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
import xyz.orbitary.bits.module.command.Async;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.module.command.Condition;
import xyz.orbitary.bits.module.command.Guard;
import xyz.orbitary.bits.module.command.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrigadierCommandBuilderTest {

    private static final List<String> calls = new ArrayList<>();
    private static final Object SOURCE = new Object();

    private final AtomicInteger asyncRuns = new AtomicInteger();

    @BeforeEach
    void clear() {
        calls.clear();
    }

    @SuppressWarnings("unused")
    public static final class Bans {
        @Command("ban")
        public void ban(@Parameter("target") String target, @Parameter(value = "days", required = false) Integer days) {
            calls.add(target + " " + days);
        }
    }

    @Command("admin")
    @SuppressWarnings("unused")
    public static final class Admin {
        @Command("")
        public void root() {
            calls.add("admin");
        }

        @Command("kick")
        public void kick(String target) {
            calls.add("kick " + target);
        }
    }

    public enum Mode { FAST, SLOW }

    @SuppressWarnings("unused")
    public static final class Modes {
        @Command("mode")
        public void mode(Mode mode) {
            calls.add("mode " + mode);
        }
    }

    public static final class Context {}

    @SuppressWarnings("unused")
    public static final class Injected {
        @Command("say")
        public void say(Context context, String message) {
            calls.add("say " + (context != null) + " " + message);
        }
    }

    public static final class DenyAll implements Condition<Object> {
        @Override
        public boolean test(Object actor) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static final class Guarded {
        @Command("secret")
        @Guard(DenyAll.class)
        public void secret() {
            calls.add("secret");
        }
    }

    @SuppressWarnings("unused")
    public static final class Later {
        @Command("later")
        @Async
        public void later() {
            calls.add("later");
        }
    }

    @SuppressWarnings("unused")
    public static final class UnsupportedType {
        @Command("odd")
        public void odd(Object value) {}
    }

    public record Point(double x, double y) {}

    @SuppressWarnings("unused")
    public static final class Points {
        @Command("point")
        public void point(Point point) {
            calls.add(point.x() + " " + point.y());
        }
    }

    @SuppressWarnings("unused")
    public static final class OptionalPoint {
        @Command("optional")
        public void optional(@Parameter(required = false) Point point) {}
    }

    @Test
    void optionalArgumentRunsWithAndWithoutIt() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Bans.class), builder());

        assertEquals(1, dispatcher.execute("ban alice", SOURCE));
        dispatcher.execute("ban alice 3", SOURCE);

        assertEquals(List.of("alice null", "alice 3"), calls);
    }

    @Test
    void missingRequiredArgumentIsASyntaxError() {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Bans.class), builder());

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("ban", SOURCE));
    }

    @Test
    void groupRootAndSubcommandsShareALiteralPath() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Admin.class), builder());

        dispatcher.execute("admin", SOURCE);
        dispatcher.execute("admin kick bob", SOURCE);

        assertEquals(List.of("admin", "kick bob"), calls);
    }

    @Test
    void enumArgumentsMatchConstantNamesIgnoringCase() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Modes.class), builder());

        dispatcher.execute("mode fast", SOURCE);

        assertEquals(List.of("mode FAST"), calls);
    }

    @Test
    void injectedParameterReceivesTheContextFactoryValue() throws CommandSyntaxException {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Injected.class, Context.class::equals), builder());

        dispatcher.execute("say hello", SOURCE);

        assertEquals(List.of("say true hello"), calls);
    }

    @Test
    void failingConditionHidesTheCommand() {
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Guarded.class), builder());

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("secret", SOURCE));
        assertEquals(List.of(), calls);
    }

    @Test
    void asyncCommandsRunOnTheGivenExecutor() throws CommandSyntaxException {
        BrigadierCommandBuilder<Object> builder = new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(),
          ignored -> null,
          (source, message) -> {},
          runnable -> {
              asyncRuns.incrementAndGet();
              runnable.run();
          }
        );
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Later.class), builder);

        dispatcher.execute("later", SOURCE);

        assertEquals(1, asyncRuns.get());
        assertEquals(List.of("later"), calls);
    }

    @Test
    void compositeParameterIsReadFromItsParts() throws CommandSyntaxException {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        arguments.registerComposite(Point.class, new BrigadierComposite<>(
          List.of(new BrigadierComposite.Part("x", Double.class), new BrigadierComposite.Part("y", Double.class)),
          values -> new Point((Double) values[0], (Double) values[1])
        ));
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Points.class), builder(arguments));

        dispatcher.execute("point 1.5 2", SOURCE);

        assertEquals(List.of("1.5 2.0"), calls);
    }

    @Test
    void optionalCompositeIsRejected() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        arguments.registerComposite(Point.class, new BrigadierComposite<>(
          List.of(new BrigadierComposite.Part("x", Double.class)),
          values -> new Point((Double) values[0], 0)
        ));
        List<CommandDefinition> commands = CommandScanner.scan(OptionalPoint.class);

        assertThrows(IllegalArgumentException.class, () -> builder(arguments).build(commands));
    }

    @Test
    void rejectsParameterTypesWithoutAnArgument() {
        List<CommandDefinition> commands = CommandScanner.scan(UnsupportedType.class);

        assertThrows(IllegalArgumentException.class, () -> builder().build(commands));
    }

    private BrigadierCommandBuilder<Object> builder() {
        return builder(BrigadierArguments.withDefaults());
    }

    private BrigadierCommandBuilder<Object> builder(BrigadierArguments<Object> arguments) {
        return new BrigadierCommandBuilder<>(arguments, ignored -> new Context(), (source, message) -> {}, Runnable::run);
    }

    private static CommandDispatcher<Object> dispatcher(List<CommandDefinition> commands, BrigadierCommandBuilder<Object> builder) {
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        builder.build(commands).forEach(dispatcher::register);
        return dispatcher;
    }

}
