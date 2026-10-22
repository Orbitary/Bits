/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;
import xyz.orbitary.bits.discord.module.command.annotation.SlashSettings;
import xyz.orbitary.bits.discord.module.command.argument.JdaArgumentRegistry;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.module.command.Parameter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlashCommandBuilderTest {

    private final SlashCommandBuilder builder = new SlashCommandBuilder(JdaArgumentRegistry.withDefaults());

    @SuppressWarnings("unused")
    public static final class Flat {
        @Command(value = "ping", description = "Pong")
        public void ping() {}

        @Command("echo")
        public void echo(@Parameter(value = "Count", description = "How many") Integer count, @Parameter(required = false) String text) {}
    }

    @Command(value = "admin", description = "Server administration")
    @SlashSettings(nsfw = true, contexts = InteractionContextType.BOT_DM, integrationTypes = IntegrationType.USER_INSTALL)
    @xyz.orbitary.bits.discord.module.command.annotation.Permission(Permission.KICK_MEMBERS)
    public static final class Admin {
        @Command("kick")
        public void kick(String target) {}

        @Command(value = "group", description = "Nested group")
        public static final class Group {
            @Command("deep")
            public void deep(String value) {}
        }
    }

    public enum Mode { FAST, SLOW }

    @SuppressWarnings("unused")
    public static final class Modes {
        @Command("mode")
        public void mode(Mode mode) {}
    }

    @SuppressWarnings("unused")
    public static final class MixedHandler {
        @Command("")
        public void handler() {}

        @Command("sub")
        public void sub() {}
    }

    @Command("too")
    @SuppressWarnings("unused")
    public static final class TooDeep {
        @Command(value = "a")
        public static final class A {
            @Command("b")
            public static final class B {
                @Command("c")
                public void c() {}
            }
        }
    }

    @Test
    void topLevelCommandUsesItsDescription() {
        SlashCommandData ping = root(Flat.class, "ping");

        assertEquals("Pong", ping.getDescription());
        assertTrue(ping.getOptions().isEmpty());
    }

    @Test
    void optionsFollowParameterTypesAndRequirement() {
        List<OptionData> options = root(Flat.class, "echo").getOptions();

        assertEquals(2, options.size());
        assertEquals("count", options.get(0).getName());
        assertEquals(OptionType.INTEGER, options.get(0).getType());
        assertTrue(options.get(0).isRequired());
        assertEquals("How many", options.get(0).getDescription());
        assertEquals(OptionType.STRING, options.get(1).getType());
        assertFalse(options.get(1).isRequired());
    }

    @Test
    void groupBuildsSubcommandsAndGroups() {
        SlashCommandData admin = root(Admin.class, "admin");

        assertEquals(1, admin.getSubcommands().size());
        assertEquals("kick", admin.getSubcommands().get(0).getName());
        assertEquals(1, admin.getSubcommandGroups().size());
        assertEquals("group", admin.getSubcommandGroups().get(0).getName());
        assertEquals("deep", admin.getSubcommandGroups().get(0).getSubcommands().get(0).getName());
    }

    @Test
    void settingsApplyToTheRootCommand() {
        SlashCommandData admin = root(Admin.class, "admin");

        assertTrue(admin.isNSFW());
        assertEquals(List.of(InteractionContextType.BOT_DM), admin.getContexts().stream().toList());
        assertEquals(List.of(IntegrationType.USER_INSTALL), admin.getIntegrationTypes().stream().toList());
    }

    @Test
    void enumArgumentsAreStringsWithAutocomplete() {
        OptionData mode = root(Modes.class, "mode").getOptions().get(0);

        assertEquals(OptionType.STRING, mode.getType());
        assertTrue(mode.isAutoComplete());
    }

    @Test
    void rejectsRootHandlerMixedWithSubcommands() {
        assertThrows(IllegalArgumentException.class, () -> builder.build(CommandScanner.scan(MixedHandler.class)));
    }

    @Test
    void rejectsCommandsNestedDeeperThanDiscordAllows() {
        assertThrows(IllegalArgumentException.class, () -> builder.build(CommandScanner.scan(TooDeep.class)));
    }

    private SlashCommandData root(Class<?> owner, String name) {
        return builder.build(CommandScanner.scan(owner)).stream()
          .filter(command -> command.getName().equals(name))
          .findFirst()
          .orElseThrow();
    }

}
