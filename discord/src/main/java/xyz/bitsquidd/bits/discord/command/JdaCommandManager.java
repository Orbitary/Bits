/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.BitsDiscord;
import xyz.bitsquidd.bits.discord.command.annotation.Command;
import xyz.bitsquidd.bits.discord.command.annotation.Parameter;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentRegistry;
import xyz.bitsquidd.bits.discord.command.util.CommandRoute;
import xyz.bitsquidd.bits.discord.command.util.JdaCommandBuilder;
import xyz.bitsquidd.bits.discord.command.util.RouteKey;
import xyz.bitsquidd.bits.lifecycle.manager.BitsModule;
import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;
import xyz.bitsquidd.bits.util.reflection.ScannerFlags;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JdaCommandManager implements BitsModule {
    private final Map<RouteKey, CommandRoute> routes = new HashMap<>();
    private final JdaArgumentRegistry argumentRegistry = new JdaArgumentRegistry();

    @Override
    public void startup() {
        List<SlashCommandData> commandData = new ArrayList<>();

        ReflectionUtils.General.createAnnotatedClassesInDir("*", Command.class, JdaCommand.class, ScannerFlags.DEFAULT)
          .stream()
          .filter(cmd -> !isNestedCommandClass(cmd.getClass()))
          .forEach(cmd -> {
              JdaCommandBuilder builder = new JdaCommandBuilder(cmd.getClass(), argumentRegistry);
              commandData.add(builder.buildCommandData(routes));
          });

        BitsDiscord.jda().updateCommands().addCommands(commandData).queue(
          ok -> Logger.success("Registered " + commandData.size() + " slash commands."),
          err -> Logger.exception("Failed to register slash commands", err)
        );

        BitsDiscord.jda().addEventListener(new CommandListener());
    }

    private boolean isNestedCommandClass(Class<?> clazz) {
        Class<?> declaring = clazz.getDeclaringClass();
        return declaring != null && declaring.isAnnotationPresent(Command.class);
    }

    private final class CommandListener extends ListenerAdapter {

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            RouteKey key = RouteKey.of(event.getName(), event.getSubcommandGroup(), event.getSubcommandName());
            CommandRoute route = routes.get(key);
            if (route == null) return;

            JdaCommandContext ctx = new JdaCommandContext(event);
            Runnable execution = () -> {
                try {
                    Object instance = route.commandClass().getDeclaredConstructor().newInstance();
                    Object[] args = resolveArgs(route.method(), event, ctx);
                    route.method().invoke(instance, args);
                } catch (Exception e) {
                    Logger.exception("Unhandled exception in command /" + event.getName(), e);
                }
            };

            if (route.isAsync()) {
                Bits.get().runLaterAsync(execution, 0);
            } else {
                execution.run();
            }
        }

        @Override
        public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
            RouteKey key = RouteKey.of(event.getName(), event.getSubcommandGroup(), event.getSubcommandName());
            CommandRoute route = routes.get(key);
            if (route == null) return;

            String focusedName = event.getFocusedOption().getName();
            String focusedValue = event.getFocusedOption().getValue();

            for (java.lang.reflect.Parameter param : route.method().getParameters()) {
                if (param.getType() == JdaCommandContext.class) continue;

                Parameter annotation = param.getAnnotation(Parameter.class);
                String paramName = resolveParamName(param, annotation);

                if (!paramName.equals(focusedName)) continue;

                JdaArgumentParser<?> parser = argumentRegistry.getParser(param.getType());
                if (!parser.supportsAutocomplete()) return;

                // Build a minimal context for autocomplete (no reply methods used)
                // ctx passed here is for guild/member access in custom parsers - not for replying!
                event.replyChoices(parser.autocomplete(focusedValue)).queue();
                return;
            }
        }

        private Object[] resolveArgs(Method method, SlashCommandInteractionEvent event, JdaCommandContext ctx) throws Exception {
            List<Object> args = new ArrayList<>();
            for (java.lang.reflect.Parameter param : method.getParameters()) {
                if (param.getType() == JdaCommandContext.class) {
                    args.add(ctx);
                    continue;
                }

                Parameter annotation = param.getAnnotation(Parameter.class);
                String name = resolveParamName(param, annotation).toLowerCase().replace(" ", "_");

                var mapping = event.getOption(name);
                if (mapping == null) {
                    args.add(null); // optional param not provided
                    continue;
                }

                JdaArgumentParser<?> parser = argumentRegistry.getParser(param.getType());
                args.add(parser.resolve(mapping, ctx));
            }
            return args.toArray();
        }

        private String resolveParamName(java.lang.reflect.Parameter param, Parameter annotation) {
            if (!annotation.value().isBlank()) return annotation.value();
            String name = param.getName();
            if (name.startsWith("arg")) return param.getType().getSimpleName().toLowerCase();
            return name;
        }

    }

}
