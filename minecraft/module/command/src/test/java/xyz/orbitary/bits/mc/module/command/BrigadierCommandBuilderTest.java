/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    public static final class MixedInjected {
        @Command("mixed")
        public void mixed(@Parameter("required") String required, Context first,
                          @Parameter(value = "optional", required = false) Integer optional, Context second) {
            calls.add(required + " " + optional + " " + (first == second) + " " + (first != null));
        }
    }

    @Test
    void optionalArgumentsStayNullWithInterleavedInjectedParameters() throws CommandSyntaxException {
        var dispatcher = dispatcher(CommandScanner.scan(MixedInjected.class, Context.class::equals), builder());
        dispatcher.execute("mixed bob", SOURCE);
        dispatcher.execute("mixed bob 3", SOURCE);
        assertEquals(List.of("bob null true true", "bob 3 true true"), calls);
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
    void failingConditionSkipsTheCommandAndReachesTheErrorHandler() throws CommandSyntaxException {
        List<Throwable> errors = new ArrayList<>();
        BrigadierCommandBuilder<Object> builder = new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(),
          ignored -> null,
          (source, error) -> errors.add(error),
          (source, node) -> true, Runnable::run
        );
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Guarded.class), builder);

        assertEquals(0, dispatcher.execute("secret", SOURCE));
        assertEquals(List.of(), calls);
        assertEquals(1, errors.size());
        assertInstanceOf(ConditionFailedException.class, errors.getFirst());
    }

    @Test
    void asyncCommandsRunOnTheGivenExecutor() throws CommandSyntaxException {
        BrigadierCommandBuilder<Object> builder = new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(),
          ignored -> null,
          (source, error) -> {},
          (source, node) -> true,
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

    @SuppressWarnings("unused")
    public static final class Crowd {
        @Command("crowd")
        public void crowd(java.util.Collection<String> names) {
            calls.add(names.toString());
        }
    }

    @SuppressWarnings("unused")
    public static final class UnregisteredCrowd {
        @Command("crowd")
        public void crowd(java.util.Collection<Integer> numbers) {}
    }

    @Test
    void collectionParameterUsesTheRegisteredElementArgument() throws CommandSyntaxException {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        arguments.registerCollection(String.class, new BrigadierArgument<>() {
            @Override
            public com.mojang.brigadier.arguments.ArgumentType<?> argumentType() {
                return com.mojang.brigadier.arguments.StringArgumentType.word();
            }

            @Override
            public Object read(com.mojang.brigadier.context.CommandContext<Object> context, String name) {
                return List.of(context.getArgument(name, String.class));
            }
        });
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Crowd.class), builder(arguments));

        dispatcher.execute("crowd bob", SOURCE);

        assertEquals(List.of("[bob]"), calls);
    }

    @Test
    void collectionWithoutRegisteredElementIsRejected() {
        List<CommandDefinition> commands = CommandScanner.scan(UnregisteredCrowd.class);

        assertThrows(IllegalArgumentException.class, () -> builder().build(commands));
    }

    @SuppressWarnings("unused")
    public static final class Reject {
        @Command("reject")
        public void reject(Bad bad) {}
    }

    public static final class Bad {}

    @Test
    void argumentErrorsReachTheErrorHandler() throws CommandSyntaxException {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        arguments.register(Bad.class, BrigadierArgument.of(StringArgumentType.word(), (context, name) -> {
            throw new SimpleCommandExceptionType(new LiteralMessage("nope")).create();
        }));
        List<String> errors = new ArrayList<>();
        BrigadierCommandBuilder<Object> builder = new BrigadierCommandBuilder<>(
          arguments,
          ignored -> null,
          (source, error) -> errors.add(error.getMessage()),
          (source, node) -> true, Runnable::run
        );
        CommandDispatcher<Object> dispatcher = dispatcher(CommandScanner.scan(Reject.class), builder);

        assertEquals(0, dispatcher.execute("reject anything", SOURCE));
        assertEquals(List.of("nope"), errors);
    }

    @Command(value = "overload", aliases = {"ol"})
    public static final class Overloaded {
        @Command("")
        public void root() { calls.add("root"); }

        @Command("")
        public void one(@Parameter("first") String first) { calls.add("one " + first); }

        @Command("")
        public void two(@Parameter("target") String target, @Parameter("amount") int amount) {
            calls.add("two " + target + " " + amount);
        }
    }

    @Test
    void overloadsDispatchByArgumentCountWithDifferentParameterNamesAndAliases() throws CommandSyntaxException {
        var commands = CommandScanner.scan(Overloaded.class);
        for (var ordered : List.of(commands, commands.reversed())) {
            calls.clear();
            var dispatcher = dispatcher(ordered, builder());
            dispatcher.execute("overload", SOURCE);
            dispatcher.execute("overload bob", SOURCE);
            dispatcher.execute("overload bob 2", SOURCE);
            dispatcher.execute("ol alice", SOURCE);
            assertEquals(List.of("root", "one bob", "two bob 2", "one alice"), calls);
        }
    }

    public static final class SecuredOverloads {
        @Command("secured")
        @Guard(DenyAll.class)
        public void one(@Parameter("first") String first) { calls.add("denied"); }

        @Command("secured")
        public void two(@Parameter("target") String target, @Parameter("amount") int amount) {
            calls.add("allowed " + target + " " + amount);
        }
    }

    @Test
    void overloadsKeepTheirOwnGuardsInEitherRegistrationOrder() throws CommandSyntaxException {
        var commands = CommandScanner.scan(SecuredOverloads.class);
        for (var ordered : List.of(commands, commands.reversed())) {
            calls.clear();
            List<Throwable> errors = new ArrayList<>();
            var builder = new BrigadierCommandBuilder<Object>(BrigadierArguments.withDefaults(), ignored -> new Context(),
              (source, error) -> errors.add(error), (source, node) -> true, Runnable::run);
            var dispatcher = dispatcher(ordered, builder);
            assertEquals(0, dispatcher.execute("secured bob", SOURCE));
            assertEquals(1, dispatcher.execute("secured bob 2", SOURCE));
            assertEquals(List.of("allowed bob 2"), calls);
            assertEquals(1, errors.size());
            assertInstanceOf(ConditionFailedException.class, errors.getFirst());
        }
    }

    public static final class OptionalOverloads {
        @Command("optionaloverload")
        public void shortForm(@Parameter(value = "first", required = false) String first) {
            calls.add("short " + first);
        }

        @Command("optionaloverload")
        public void longForm(@Parameter("target") String target, @Parameter("amount") int amount) {
            calls.add("long " + target + " " + amount);
        }
    }

    @Test
    void disjointOptionalOverloadsDispatchAtEachAcceptedArgumentCount() throws CommandSyntaxException {
        var commands = CommandScanner.scan(OptionalOverloads.class);
        for (var ordered : List.of(commands, commands.reversed())) {
            calls.clear();
            var dispatcher = dispatcher(ordered, builder());
            dispatcher.execute("optionaloverload", SOURCE);
            dispatcher.execute("optionaloverload bob", SOURCE);
            dispatcher.execute("optionaloverload bob 2", SOURCE);
            assertEquals(List.of("short null", "short bob", "long bob 2"), calls);
        }
    }

    public static final class PrimitiveOverloads {
        @Command("primitiveoverload")
        public void shortForm(@Parameter("first") int first) { calls.add("short " + first); }

        @Command("primitiveoverload")
        public void longForm(@Parameter("value") Integer value, @Parameter("amount") int amount) {
            calls.add("long " + value + " " + amount);
        }
    }

    @Test
    void primitiveAndWrapperOverloadsShareTheirCommonParser() throws CommandSyntaxException {
        var dispatcher = dispatcher(CommandScanner.scan(PrimitiveOverloads.class), builder());
        dispatcher.execute("primitiveoverload 1", SOURCE);
        dispatcher.execute("primitiveoverload 1 2", SOURCE);
        assertEquals(List.of("short 1", "long 1 2"), calls);
    }

    public static final class IncompatibleOverloads {
        @Command("incompatible")
        public void shortForm(int first) {}

        @Command("incompatible")
        public void longForm(String first, int second) {}
    }

    @Test
    void incompatibleOverloadPrefixesAreRejectedBeforeRegistration() {
        assertThrows(IllegalArgumentException.class, () -> builder().build(CommandScanner.scan(IncompatibleOverloads.class)));
    }

    public static final class AliasedOverloads {
        @Command(value = "aliasoverload", aliases = {"shortalias", "sharedalias"})
        public void root() { calls.add("root"); }

        @Command(value = "aliasoverload", aliases = {"longalias", "sharedalias"})
        public void argument(String value) { calls.add("argument " + value); }
    }

    @Test
    void overloadAliasesAreCombinedRegardlessOfRegistrationOrder() throws CommandSyntaxException {
        var commands = CommandScanner.scan(AliasedOverloads.class);
        for (var ordered : List.of(commands, commands.reversed())) {
            calls.clear();
            var dispatcher = dispatcher(ordered, builder());
            for (var alias : List.of("aliasoverload", "shortalias", "longalias", "sharedalias")) {
                dispatcher.execute(alias, SOURCE);
                dispatcher.execute(alias + " bob", SOURCE);
            }
            assertEquals(List.of("root", "argument bob", "root", "argument bob",
              "root", "argument bob", "root", "argument bob"), calls);
        }
    }

    public static final class AliasConflictsWithCommand {
        @Command(value = "first", aliases = {"second"})
        public void first() {}

        @Command("second")
        public void second() {}
    }

    public static final class AliasConflictsWithAlias {
        @Command(value = "first", aliases = {"shared"})
        public void first() {}

        @Command(value = "second", aliases = {"shared"})
        public void second() {}
    }

    @Test
    void aliasCollisionsAreRejectedBeforeRegistration() {
        for (var owner : List.of(AliasConflictsWithCommand.class, AliasConflictsWithAlias.class)) {
            var commands = CommandScanner.scan(owner);
            for (var ordered : List.of(commands, commands.reversed())) {
                var error = assertThrows(IllegalArgumentException.class, () -> builder().build(ordered));
                assertEquals(true, String.valueOf(error.getMessage()).contains("conflicts"));
            }
        }
    }

    @Command("prepared")
    @Async
    public static final class Prepared {
        public Prepared(@Parameter("initial") Bad initial) { calls.add("constructed"); }

        @Command("run")
        public void run(Context context, @Parameter("value") Bad value) { calls.add("invoked"); }
    }

    @Test
    void asyncArgumentsAndContextAreResolvedBeforeScheduling() throws CommandSyntaxException {
        var arguments = BrigadierArguments.<Object>withDefaults();
        var caller = Thread.currentThread();
        arguments.register(Bad.class, BrigadierArgument.of(StringArgumentType.word(), (context, name) -> {
            assertEquals(caller, Thread.currentThread());
            calls.add("parsed " + name);
            return new Bad();
        }));
        List<Runnable> queued = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        var builder = new BrigadierCommandBuilder<Object>(arguments, ignored -> {
            calls.add("context");
            return new Context();
        }, (source, error) -> errors.add(error), (source, node) -> true, queued::add);
        var dispatcher = dispatcher(CommandScanner.scan(Prepared.class, Context.class::equals), builder);

        assertEquals(1, dispatcher.execute("prepared first run second", SOURCE));
        assertEquals(List.of("parsed initial", "context", "parsed value"), calls);
        assertEquals(1, queued.size());
        queued.getFirst().run();
        assertEquals(List.of("parsed initial", "context", "parsed value", "constructed", "invoked"), calls);
        assertEquals(List.of(), errors);
    }

    @Test
    void asyncArgumentFailureDoesNotScheduleExecution() throws CommandSyntaxException {
        var arguments = BrigadierArguments.<Object>withDefaults();
        arguments.register(Bad.class, BrigadierArgument.of(StringArgumentType.word(), (context, name) -> {
            throw new SimpleCommandExceptionType(new LiteralMessage("invalid argument")).create();
        }));
        List<Runnable> queued = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        var builder = new BrigadierCommandBuilder<Object>(arguments, ignored -> new Context(),
          (source, error) -> errors.add(error), (source, node) -> true, queued::add);
        var dispatcher = dispatcher(CommandScanner.scan(Prepared.class, Context.class::equals), builder);

        assertEquals(0, dispatcher.execute("prepared first run second", SOURCE));
        assertEquals(List.of(), queued);
        assertEquals(List.of(), calls);
        assertInstanceOf(CommandSyntaxException.class, errors.getFirst());
    }

    @Test
    void deniedAsyncCommandDoesNotParseOrSchedule() throws CommandSyntaxException {
        var arguments = BrigadierArguments.<Object>withDefaults();
        arguments.register(Bad.class, BrigadierArgument.of(StringArgumentType.word(), (context, name) -> {
            calls.add("parsed");
            return new Bad();
        }));
        List<Runnable> queued = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        var builder = new BrigadierCommandBuilder<Object>(arguments, ignored -> new Context(),
          (source, error) -> errors.add(error), (source, node) -> false, queued::add);
        var dispatcher = dispatcher(CommandScanner.scan(Prepared.class, Context.class::equals), builder);

        assertEquals(0, dispatcher.execute("prepared first run second", SOURCE));
        assertEquals(List.of(), queued);
        assertEquals(List.of(), calls);
        assertInstanceOf(PermissionDeniedException.class, errors.getFirst());
    }

    private BrigadierCommandBuilder<Object> builder() {
        return builder(BrigadierArguments.withDefaults());
    }

    private BrigadierCommandBuilder<Object> builder(BrigadierArguments<Object> arguments) {
        return new BrigadierCommandBuilder<>(arguments, ignored -> new Context(), (source, error) -> {}, (source, node) -> true, Runnable::run);
    }

    private static CommandDispatcher<Object> dispatcher(List<CommandDefinition> commands, BrigadierCommandBuilder<Object> builder) {
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        builder.build(commands).forEach(dispatcher::register);
        return dispatcher;
    }

}
