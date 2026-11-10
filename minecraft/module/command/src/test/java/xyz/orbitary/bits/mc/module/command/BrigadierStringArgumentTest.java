/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrigadierStringArgumentTest {
    private static final Object SOURCE = new Object();

    private static final class Counting implements BrigadierStringArgument<Object, String> {
        final AtomicInteger valueReads = new AtomicInteger();

        @Override
        public String parse(String input) throws CommandSyntaxException {
            if (input.equals("bad")) throw BrigadierArgument.error("bad input");
            return input.toUpperCase();
        }

        @Override
        public Collection<String> values() {
            valueReads.incrementAndGet();
            return List.of("alpha", "beta");
        }
    }

    private static CommandDispatcher<Object> dispatcher(Counting argument, AtomicReference<Object> received) {
        RequiredArgumentBuilder<Object, ?> value = RequiredArgumentBuilder.argument("value", argument.argumentType());
        value.suggests(argument.suggestions());
        value.executes(context -> {
            received.set(argument.read(context, "value"));
            return 1;
        });

        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        dispatcher.register(LiteralArgumentBuilder.<Object>literal("pick").then(value));
        return dispatcher;
    }

    @Test
    void valuesAreReadWhenCompletingNotWhenBuilding() throws Exception {
        Counting argument = new Counting();
        CommandDispatcher<Object> dispatcher = dispatcher(argument, new AtomicReference<>());
        assertEquals(0, argument.valueReads.get());

        var suggestions = dispatcher.getCompletionSuggestions(dispatcher.parse("pick a", SOURCE)).get();

        assertEquals(1, argument.valueReads.get());
        assertEquals(List.of("alpha"), suggestions.getList().stream().map(suggestion -> suggestion.getText()).toList());
    }

    @Test
    void readParsesTheTypedString() throws Exception {
        AtomicReference<Object> received = new AtomicReference<>();

        dispatcher(new Counting(), received).execute("pick abc", SOURCE);

        assertEquals("ABC", received.get());
    }

    @Test
    void parseErrorsCarryTheirMessage() {
        var error = assertThrows(CommandSyntaxException.class, () -> dispatcher(new Counting(), new AtomicReference<>()).execute("pick bad", SOURCE));

        assertEquals("bad input", error.getRawMessage().getString());
    }

}
