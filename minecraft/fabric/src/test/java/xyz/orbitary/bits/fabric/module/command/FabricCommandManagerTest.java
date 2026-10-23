/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.fabric.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import xyz.orbitary.bits.log.BasicLogger;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.mc.module.command.BrigadierArgument;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;
import xyz.orbitary.bits.mc.module.command.ConditionFailedException;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.Condition;
import xyz.orbitary.bits.module.command.Guard;
import xyz.orbitary.bits.module.command.Parameter;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FabricCommandManagerTest {

    private static final List<String> calls = new ArrayList<>();
    private static final List<Throwable> errors = new ArrayList<>();
    private static final List<BrigadierArguments<String>> hooked = new ArrayList<>();
    private static final String SOURCE = "source";

    @BeforeAll
    static void setUp() {
        new BasicLogger(LoggerFactory.getLogger("test"), Logger.LogFlags.defaultFlags());
        ReflectionUtils.setClassloader(FabricCommandManagerTest.class.getClassLoader());
    }

    @BeforeEach
    void clear() {
        calls.clear();
        errors.clear();
        hooked.clear();
    }

    public record Shout(String word) {}

    @Command("greet")
    @SuppressWarnings("unused")
    public static final class Greet {
        @Command("hello")
        public void hello(@Parameter("who") String who) {
            calls.add("hello " + who);
        }

        @Command("shout")
        public void shout(@Parameter("word") Shout shout) {
            calls.add("shout " + shout.word());
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

    public static final class DenyAll implements Condition<String> {
        @Override
        public boolean test(String source) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static final class Locked {
        @Command("locked")
        @Guard(DenyAll.class)
        public void locked() {
            calls.add("locked");
        }
    }

    @SuppressWarnings("unused")
    public static final class Failing {
        @Command("fail")
        public void fail() {
            throw new IllegalStateException("broken");
        }
    }

    private static FabricCommandManager<String> manager(List<Consumer<CommandDispatcher<String>>> callbacks, Consumer<BrigadierArguments<String>> custom) {
        return new FabricCommandManager<>(
          custom,
          (source, error) -> errors.add(error),
          (source, node) -> true,
          callbacks::add
        );
    }

    private static BrigadierArguments<String> shoutArgument(BrigadierArguments<String> arguments) {
        arguments.register(Shout.class, new BrigadierArgument<>() {
            @Override
            public com.mojang.brigadier.arguments.ArgumentType<?> argumentType() {
                return StringArgumentType.word();
            }

            @Override
            public Object read(CommandContext<String> context, String name) {
                return new Shout(context.getArgument(name, String.class));
            }
        });
        return arguments;
    }

    private static CommandDispatcher<String> registered(List<Consumer<CommandDispatcher<String>>> callbacks) {
        CommandDispatcher<String> dispatcher = new CommandDispatcher<>();
        callbacks.getFirst().accept(dispatcher);
        return dispatcher;
    }

    @Test
    void startupHandsTheRegistrationCallbackToThePlatform() {
        List<Consumer<CommandDispatcher<String>>> callbacks = new ArrayList<>();

        manager(callbacks, FabricCommandManagerTest::shoutArgument).startup();

        assertEquals(1, callbacks.size());
    }

    @Test
    void registeredCommandsRunTheirMethods() throws CommandSyntaxException {
        List<Consumer<CommandDispatcher<String>>> callbacks = new ArrayList<>();
        manager(callbacks, FabricCommandManagerTest::shoutArgument).startup();

        assertEquals(1, registered(callbacks).execute("greet hello bob", SOURCE));
        assertEquals(List.of("hello bob"), calls);
    }

    @Test
    void everyDispatcherGetsTheCommands() throws CommandSyntaxException {
        List<Consumer<CommandDispatcher<String>>> callbacks = new ArrayList<>();
        manager(callbacks, FabricCommandManagerTest::shoutArgument).startup();

        CommandDispatcher<String> first = new CommandDispatcher<>();
        CommandDispatcher<String> second = new CommandDispatcher<>();
        callbacks.getFirst().accept(first);
        callbacks.getFirst().accept(second);
        first.execute("mode fast", SOURCE);
        second.execute("mode slow", SOURCE);

        assertEquals(List.of("mode FAST", "mode SLOW"), calls);
    }

    @Test
    void enumArgumentsParseFromTheDefaults() throws CommandSyntaxException {
        List<Consumer<CommandDispatcher<String>>> callbacks = new ArrayList<>();
        manager(callbacks, FabricCommandManagerTest::shoutArgument).startup();

        registered(callbacks).execute("mode slow", SOURCE);

        assertEquals(List.of("mode SLOW"), calls);
    }

    @Test
    void customArgumentsFromThePlatformAreUsed() throws CommandSyntaxException {
        List<Consumer<CommandDispatcher<String>>> callbacks = new ArrayList<>();
        manager(callbacks, arguments -> {
            hooked.add(arguments);
            shoutArgument(arguments);
        }).startup();

        registered(callbacks).execute("greet shout hey", SOURCE);

        assertEquals(1, hooked.size());
        assertEquals(List.of("shout hey"), calls);
    }

    @Test
    void failingConditionSkipsTheCommandAndReportsIt() throws CommandSyntaxException {
        List<Consumer<CommandDispatcher<String>>> callbacks = new ArrayList<>();
        manager(callbacks, FabricCommandManagerTest::shoutArgument).startup();

        assertEquals(0, registered(callbacks).execute("locked", SOURCE));
        assertTrue(calls.isEmpty());
        assertEquals(1, errors.size());
        assertInstanceOf(ConditionFailedException.class, errors.getFirst());
    }

    @Test
    void thrownErrorsReachTheErrorHandler() throws CommandSyntaxException {
        List<Consumer<CommandDispatcher<String>>> callbacks = new ArrayList<>();
        manager(callbacks, FabricCommandManagerTest::shoutArgument).startup();

        assertEquals(0, registered(callbacks).execute("fail", SOURCE));
        assertEquals(1, errors.size());
        assertInstanceOf(IllegalStateException.class, errors.getFirst());
    }

}
