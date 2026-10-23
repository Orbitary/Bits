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
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.module.command.Permission;
import xyz.orbitary.bits.module.command.PermissionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BrigadierPermissionTest {

    private static final List<String> calls = new ArrayList<>();
    private static final List<Throwable> errors = new ArrayList<>();
    private static final String SOURCE = "source";

    @BeforeEach
    void clear() {
        calls.clear();
        errors.clear();
    }

    @SuppressWarnings("unused")
    public static final class Ban {
        @Command("ban")
        @Permission("ops.ban")
        public void ban() {
            calls.add("ban");
        }

        @Command("kick")
        @Permission(value = {"ops.kick"}, type = PermissionType.ALL)
        public void kick() {
            calls.add("kick");
        }
    }

    private static CommandDispatcher<String> dispatcher(Set<String> granted) {
        BrigadierCommandBuilder<String> builder = new BrigadierCommandBuilder<>(
          BrigadierArguments.withDefaults(),
          ignored -> null,
          (source, error) -> errors.add(error),
          (source, node) -> granted.contains(node),
          Runnable::run
        );
        CommandDispatcher<String> dispatcher = new CommandDispatcher<>();
        builder.build(CommandScanner.scan(Ban.class)).forEach(dispatcher::register);
        return dispatcher;
    }

    @Test
    void sourceWithoutAnyNodeIsDenied() throws CommandSyntaxException {
        assertEquals(0, dispatcher(Set.of()).execute("ban", SOURCE));
        assertEquals(List.of(), calls);
        assertEquals(1, errors.size());
        assertInstanceOf(PermissionDeniedException.class, errors.getFirst());
    }

    @Test
    void defaultNodeRunsTheCommand() throws CommandSyntaxException {
        assertEquals(1, dispatcher(Set.of("bits.command.ban")).execute("ban", SOURCE));
        assertEquals(List.of("ban"), calls);
    }

    @Test
    void extraNodeIsAnAlternativeToTheDefault() throws CommandSyntaxException {
        dispatcher(Set.of("ops.ban")).execute("ban", SOURCE);

        assertEquals(List.of("ban"), calls);
    }

    @Test
    void allRequirementNeedsTheDefaultAndEveryListedNode() throws CommandSyntaxException {
        CommandDispatcher<String> dispatcher = dispatcher(Set.of("bits.command.kick", "ops.kick"));

        dispatcher.execute("kick", SOURCE);

        assertEquals(List.of("kick"), calls);
    }

    @Test
    void allRequirementIsDeniedWithoutEveryNode() throws CommandSyntaxException {
        assertEquals(0, dispatcher(Set.of("bits.command.kick")).execute("kick", SOURCE));
        assertEquals(List.of(), calls);
    }

}
