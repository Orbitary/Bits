/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.junit.jupiter.api.Test;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.module.command.Suggest;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tab completion comes from the argument that parses each value: a custom type's own suggestions, the native
 * suggestions of a Brigadier type, and enum constants. No annotation is involved.
 */
class BrigadierCustomSuggestionsTest {

    private static final Object SOURCE = new Object();
    private static final List<String> COLORS = List.of("red", "green", "blue");

    public record Color(String name) {}

    public record Point(double x, double y) {}

    @SuppressWarnings("unused")
    public static final class PaintExtra {
        @Command("paint")
        public void paint(@Suggest({"all", "none"}) Color color) {}
    }

    @SuppressWarnings("unused")
    public static final class FlagExtra {
        @Command("flag")
        public void flag(@Suggest("maybe") boolean on) {}
    }

    @SuppressWarnings("unused")
    public static final class ModeExtra {
        @Command("mode")
        public void mode(@Suggest("custom") Mode mode) {}
    }

    @SuppressWarnings("unused")
    public static final class PaintFirst {
        @Command("paint")
        public void paint(@Suggest({"zebra", "apple"}) Color color) {}
    }

    public record Pair(String first, String second) {}

    @SuppressWarnings("unused")
    public static final class PairSuggest {
        @Command("pair")
        public void pair(@Suggest({"1 2", "3 4"}) Pair pair) {}
    }

    @SuppressWarnings("unused")
    public static final class PointExtra {
        @Command("pt")
        public void pt(@Suggest("origin") Point point) {}
    }

    public enum Mode { FAST, SLOW }

    @SuppressWarnings("unused")
    public static final class Paint {
        @Command("paint")
        public void paint(Color color) {}
    }

    @Command("paint")
    @SuppressWarnings("unused")
    public static final class PaintWithClear {
        @Command("")
        public void paint(Color color) {}

        @Command("clear")
        public void clear() {}
    }

    @SuppressWarnings("unused")
    public static final class Many {
        @Command("many")
        public void many(Collection<Color> colors) {}
    }

    @SuppressWarnings("unused")
    public static final class Flag {
        @Command("flag")
        public void flag(boolean on) {}
    }

    @SuppressWarnings("unused")
    public static final class Modes {
        @Command("mode")
        public void mode(Mode mode) {}
    }

    private static BrigadierArgument<Object> color() {
        return new BrigadierArgument<>() {
            @Override
            public ArgumentType<?> argumentType() {
                return StringArgumentType.word();
            }

            @Override
            public Object read(CommandContext<Object> context, String name) {
                return new Color(context.getArgument(name, String.class));
            }

            @Override
            public SuggestionProvider<Object> suggestions() {
                return BrigadierArgument.suggest(COLORS);
            }
        };
    }

    private static BrigadierArguments<Object> arguments() {
        BrigadierArguments<Object> arguments = BrigadierArguments.withDefaults();
        arguments.register(Color.class, color());
        arguments.registerComposite(Point.class, new BrigadierComposite<>(
          List.of(new BrigadierComposite.Part("x", Double.class), new BrigadierComposite.Part("y", Double.class)),
          values -> new Point((Double) values[0], (Double) values[1])
        ));
        arguments.registerComposite(Pair.class, new BrigadierComposite<>(
          List.of(new BrigadierComposite.Part("first", String.class), new BrigadierComposite.Part("second", String.class)),
          values -> new Pair((String) values[0], (String) values[1])
        ));
        arguments.registerCollection(Color.class, new BrigadierArgument<>() {
            @Override
            public ArgumentType<?> argumentType() {
                return StringArgumentType.word();
            }

            @Override
            public Object read(CommandContext<Object> context, String name) {
                return List.of(new Color(context.getArgument(name, String.class)));
            }

            @Override
            public SuggestionProvider<Object> suggestions() {
                return BrigadierArgument.suggest(COLORS);
            }
        });
        return arguments;
    }

    private static CommandDispatcher<Object> dispatcher(Class<?> owner) {
        BrigadierCommandBuilder<Object> builder = new BrigadierCommandBuilder<>(
          arguments(),
          ignored -> null,
          (source, error) -> {},
          Runnable::run
        );
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        List<CommandDefinition> commands = CommandScanner.scan(owner);
        builder.build(commands).forEach(dispatcher::register);
        return dispatcher;
    }

    private static List<String> ordered(CommandDispatcher<Object> dispatcher, String input) throws Exception {
        return dispatcher.getCompletionSuggestions(dispatcher.parse(input, SOURCE)).get().getList().stream()
          .map(suggestion -> suggestion.getText())
          .toList();
    }

    private static List<String> completions(CommandDispatcher<Object> dispatcher, String input) throws Exception {
        return dispatcher.getCompletionSuggestions(dispatcher.parse(input, SOURCE)).get().getList().stream()
          .map(suggestion -> suggestion.getText())
          .sorted()
          .toList();
    }

    @Test
    void suggestValuesAddToTheCustomArgumentsValues() throws Exception {
        assertEquals(List.of("all", "blue", "green", "none", "red"), completions(dispatcher(PaintExtra.class), "paint "));
    }

    @Test
    void suggestValuesAppearAlongsideTheArgumentsOwnValues() throws Exception {
        assertEquals(List.of("apple", "blue", "green", "red", "zebra"), completions(dispatcher(PaintFirst.class), "paint "));
    }

    @Test
    void suggestOnCompositeOffersEachPartsTokens() throws Exception {
        CommandDispatcher<Object> dispatcher = dispatcher(PairSuggest.class);

        assertEquals(List.of("1", "3"), ordered(dispatcher, "pair "));
        assertEquals(List.of("2", "4"), ordered(dispatcher, "pair 1 "));
    }

    @Test
    void suggestValuesAreFilteredByWhatIsTyped() throws Exception {
        assertEquals(List.of("all"), completions(dispatcher(PaintExtra.class), "paint a"));
    }

    @Test
    void suggestValuesAddToNativeCompletions() throws Exception {
        assertEquals(List.of("false", "maybe", "true"), completions(dispatcher(FlagExtra.class), "flag "));
    }

    @Test
    void suggestValuesAddToEnumConstants() throws Exception {
        assertEquals(List.of("custom", "fast", "slow"), completions(dispatcher(ModeExtra.class), "mode "));
    }

    @Test
    void suggestOnCompositeArgumentIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> dispatcher(PointExtra.class));
    }

    @Test
    void customArgumentOffersItsOwnValues() throws Exception {
        assertEquals(List.of("blue", "green", "red"), completions(dispatcher(Paint.class), "paint "));
    }

    @Test
    void customArgumentFiltersByWhatIsTyped() throws Exception {
        assertEquals(List.of("red"), completions(dispatcher(Paint.class), "paint r"));
    }

    @Test
    void customCompletionsSitBesideSubcommandNames() throws Exception {
        assertEquals(List.of("blue", "clear", "green", "red"), completions(dispatcher(PaintWithClear.class), "paint "));
    }

    @Test
    void collectionElementsUseTheirOwnCompletions() throws Exception {
        assertEquals(List.of("blue", "green", "red"), completions(dispatcher(Many.class), "many "));
    }

    @Test
    void nativeBrigadierCompletionsStillApply() throws Exception {
        assertEquals(List.of("false", "true"), completions(dispatcher(Flag.class), "flag "));
    }

    @Test
    void enumConstantsAreOfferedInLowerCase() throws Exception {
        assertEquals(List.of("fast", "slow"), completions(dispatcher(Modes.class), "mode "));
    }

}
