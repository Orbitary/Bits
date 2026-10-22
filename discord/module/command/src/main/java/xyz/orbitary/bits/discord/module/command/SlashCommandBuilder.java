/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command;

import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.Nullable;
import xyz.orbitary.bits.discord.module.command.annotation.Permission;
import xyz.orbitary.bits.discord.module.command.annotation.SlashSettings;
import xyz.orbitary.bits.discord.module.command.argument.JdaArgumentParser;
import xyz.orbitary.bits.discord.module.command.argument.JdaArgumentRegistry;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandDefinition.Segment;
import xyz.orbitary.bits.module.command.ParameterDefinition;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Turns command definitions into Discord slash command data. Discord allows three levels: command, subcommand
 * group, and subcommand.
 *
 * @since 0.1.6
 */
final class SlashCommandBuilder {

    private static final String NO_DESCRIPTION = "No description provided.";
    private static final IntegrationType[] DEFAULT_INTEGRATIONS = {IntegrationType.GUILD_INSTALL};
    private static final InteractionContextType[] DEFAULT_CONTEXTS = {InteractionContextType.GUILD};

    private final JdaArgumentRegistry arguments;

    SlashCommandBuilder(JdaArgumentRegistry arguments) {
        this.arguments = arguments;
    }

    /**
     * @param definitions the commands to build, grouped by their first segment
     *
     * @return one slash command per distinct first segment
     *
     * @throws IllegalArgumentException if a command is nested deeper than Discord allows, or mixes a root handler
     *                                  with subcommands
     *
     * @since 0.1.6
     */
    List<SlashCommandData> build(List<CommandDefinition> definitions) {
        Map<String, List<CommandDefinition>> roots = definitions.stream()
          .collect(Collectors.groupingBy(command -> command.path().get(0).name(), LinkedHashMap::new, Collectors.toList()));
        return roots.values().stream().map(this::root).toList();
    }

    private SlashCommandData root(List<CommandDefinition> commands) {
        Segment segment = commands.get(0).path().get(0);
        SlashCommandData data = Commands.slash(segment.name(), descriptionOrDefault(segment.description()));
        applySettings(commands, data);

        CommandDefinition handler = commands.stream().filter(command -> command.path().size() == 1).findFirst().orElse(null);
        if (handler != null) {
            if (commands.size() > 1) {
                throw new IllegalArgumentException("Command /" + segment.name() + " has both a root handler and subcommands");
            }
            data.addOptions(options(handler));
            return data;
        }

        Map<String, SubcommandGroupData> groups = new LinkedHashMap<>();
        for (CommandDefinition command : commands) {
            List<Segment> path = command.path();
            switch (path.size()) {
                case 2 -> data.addSubcommands(subcommand(command, path.get(1)));
                case 3 -> groups.computeIfAbsent(path.get(1).name(), name -> new SubcommandGroupData(name, descriptionOrDefault(path.get(1).description())))
                  .addSubcommands(subcommand(command, path.get(2)));
                default -> throw new IllegalArgumentException("Command /" + command.fullName() + " is nested deeper than Discord allows");
            }
        }
        data.addSubcommandGroups(groups.values());
        return data;
    }

    private SubcommandData subcommand(CommandDefinition command, Segment segment) {
        return new SubcommandData(segment.name(), descriptionOrDefault(segment.description())).addOptions(options(command));
    }

    private List<OptionData> options(CommandDefinition command) {
        return command.parameters().stream().map(this::option).toList();
    }

    private OptionData option(ParameterDefinition parameter) {
        JdaArgumentParser<?> parser = arguments.parser(parameter.type());
        OptionData option = new OptionData(parser.optionType(), parameter.name(), descriptionOrDefault(parameter.description()), parameter.required());
        if (parser.supportsAutocomplete()) option.setAutoComplete(true);
        return option;
    }

    private void applySettings(List<CommandDefinition> commands, SlashCommandData data) {
        SlashSettings settings = find(SlashSettings.class, commands);
        data.setIntegrationTypes(settings == null ? DEFAULT_INTEGRATIONS : settings.integrationTypes());
        data.setContexts(settings == null ? DEFAULT_CONTEXTS : settings.contexts());
        data.setNSFW(settings != null && settings.nsfw());

        Permission permission = find(Permission.class, commands);
        if (permission != null) data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permission.value()));
    }

    // Settings are declared on the command class or an enclosing class, so search outwards from each command.
    private static <A extends Annotation> @Nullable A find(Class<A> type, List<CommandDefinition> commands) {
        for (CommandDefinition command : commands) {
            for (Class<?> owner = command.owner(); owner != null; owner = owner.getEnclosingClass()) {
                A annotation = owner.getAnnotation(type);
                if (annotation != null) return annotation;
            }
        }
        return null;
    }

    private static String descriptionOrDefault(String description) {
        return description.isBlank() ? NO_DESCRIPTION : description;
    }

}
