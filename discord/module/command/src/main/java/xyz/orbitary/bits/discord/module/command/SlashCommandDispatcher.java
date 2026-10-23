/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import xyz.orbitary.bits.Bits;
import xyz.orbitary.bits.discord.module.command.argument.JdaArgumentRegistry;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.module.command.Condition;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.ParameterDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Runs slash commands by routing each interaction to its {@link CommandDefinition}.
 *
 * @since 0.1.6
 */
final class SlashCommandDispatcher extends ListenerAdapter {

    private final Map<String, CommandDefinition> routes;
    private final JdaArgumentRegistry arguments;

    /**
     * @param routes    commands keyed by their space-separated path, as {@code getFullCommandName()} reports them
     * @param arguments the parsers used to convert option values
     *
     * @since 0.1.6
     */
    SlashCommandDispatcher(Map<String, CommandDefinition> routes, JdaArgumentRegistry arguments) {
        this.routes = routes;
        this.arguments = arguments;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        CommandDefinition command = routes.get(event.getFullCommandName());
        if (command == null) return;

        Runnable execution = () -> execute(command, event);
        if (command.async()) {
            Bits.get().runLaterAsync(execution, 0);
        } else {
            execution.run();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        CommandDefinition command = routes.get(event.getFullCommandName());
        if (command == null) return;

        String focusedName = event.getFocusedOption().getName();
        for (ParameterDefinition parameter : command.parameters()) {
            if (!parameter.name().equals(focusedName)) continue;

            var parser = arguments.parser(parameter.rawType());
            if (parser.supportsAutocomplete()) {
                event.replyChoices(parser.autocomplete(event.getFocusedOption().getValue())).queue();
            } else {
                event.replyChoices(List.of()).queue();
            }
            return;
        }
    }

    private void execute(CommandDefinition command, SlashCommandInteractionEvent event) {
        try {
            if (!passesConditions(command, event)) return;

            JdaCommandContext context = new JdaCommandContext(event);
            Object owner = newInstance(command.owner());
            command.method().invoke(owner, resolveArguments(command, event, context));
        } catch (Exception e) {
            Logger.exception("Unhandled exception in command /" + event.getFullCommandName(), e);
        }
    }

    private boolean passesConditions(CommandDefinition command, SlashCommandInteractionEvent event) throws ReflectiveOperationException {
        for (Class<? extends Condition<?>> conditionClass : command.conditions()) {
            JdaCommandGuard guard = (JdaCommandGuard) newInstance(conditionClass);
            if (guard.test(event)) continue;

            MessageEmbed denied = guard.denied();
            if (denied != null) {
                event.replyEmbeds(denied).setEphemeral(true).queue();
            } else {
                event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            }
            return false;
        }
        return true;
    }

    private Object[] resolveArguments(CommandDefinition command, SlashCommandInteractionEvent event, JdaCommandContext context) {
        // Parameters the platform injects are the context; command arguments overwrite their slots below.
        Object[] args = new Object[command.method().getParameterCount()];
        Arrays.fill(args, context);

        for (ParameterDefinition parameter : command.parameters()) {
            OptionMapping option = event.getOption(parameter.name());
            args[parameter.index()] = option == null ? null : arguments.parser(parameter.rawType()).resolve(option, context);
        }
        return args;
    }

    private static Object newInstance(Class<?> type) throws ReflectiveOperationException {
        return type.getDeclaredConstructor().newInstance();
    }

}
