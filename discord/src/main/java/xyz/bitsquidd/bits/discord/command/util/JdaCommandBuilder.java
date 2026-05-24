/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.util;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import xyz.bitsquidd.bits.discord.command.JdaCommand;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.annotation.Async;
import xyz.bitsquidd.bits.discord.command.annotation.Command;
import xyz.bitsquidd.bits.discord.command.annotation.Parameter;
import xyz.bitsquidd.bits.discord.command.annotation.Permission;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentRegistry;
import xyz.bitsquidd.bits.log.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Builds {@link SlashCommandData} and populates the dispatch route table
 * from a {@link JdaCommand} class annotated with {@link Command}.
 */
public final class JdaCommandBuilder {
    private final Class<? extends JdaCommand> commandClass;
    private final Command rootAnnotation;
    private final JdaArgumentRegistry registry;

    public JdaCommandBuilder(Class<? extends JdaCommand> commandClass, JdaArgumentRegistry registry) {
        this.commandClass = commandClass;
        this.rootAnnotation = commandClass.getAnnotation(Command.class);
        this.registry = registry;
    }

    public SlashCommandData buildCommandData(Map<RouteKey, CommandRoute> routes) {
        String name = rootAnnotation.value();
        String desc = rootAnnotation.description().isBlank() ? "No description provided." : rootAnnotation.description();

        SlashCommandData cmd = Commands.slash(name, desc)
          .setIntegrationTypes(rootAnnotation.integrationType())
          .setNSFW(rootAnnotation.nsfw());

        applyPermission(cmd);

        List<Method> subcommandMethods = getCommandMethods(commandClass);
        List<Class<?>> subcommandGroups = getCommandNestedClasses(commandClass);

        if (subcommandMethods.isEmpty() && subcommandGroups.isEmpty()) {
            // Root command: route to execute()
            Method execute = findExecuteMethod(commandClass);
            if (execute != null) {
                cmd.addOptions(buildOptions(execute));
                routes.put(RouteKey.of(name, "", ""), route(commandClass, execute));
            } else {
                Logger.warn("Command /" + name + " has no execute() method and no subcommands — skipping.");
            }
        } else {
            // Direct subcommand methods
            for (Method method : subcommandMethods) {
                Command meta = method.getAnnotation(Command.class);
                SubcommandData sub = new SubcommandData(meta.value(), descOrDefault(meta.description()));
                sub.addOptions(buildOptions(method));
                cmd.addSubcommands(sub);
                routes.put(RouteKey.of(name, "", meta.value()), route(commandClass, method));
            }

            // Subcommand groups (nested static classes)
            for (Class<?> groupClass : subcommandGroups) {
                Command groupMeta = groupClass.getAnnotation(Command.class);
                SubcommandGroupData group = new SubcommandGroupData(groupMeta.value(), descOrDefault(groupMeta.description()));

                for (Method method : getCommandMethods(groupClass)) {
                    Command subMeta = method.getAnnotation(Command.class);
                    SubcommandData sub = new SubcommandData(subMeta.value(), descOrDefault(subMeta.description()));
                    sub.addOptions(buildOptions(method));
                    group.addSubcommands(sub);

                    @SuppressWarnings("unchecked")
                    Class<? extends JdaCommand> groupJdaClass = (Class<? extends JdaCommand>) groupClass;
                    routes.put(RouteKey.of(name, groupMeta.value(), subMeta.value()), route(groupJdaClass, method));
                }

                cmd.addSubcommandGroups(group);
            }
        }

        Logger.info("Built command: /" + name);
        return cmd;
    }

    private List<OptionData> buildOptions(Method method) {
        List<OptionData> options = new ArrayList<>();
        java.lang.reflect.Parameter[] params = method.getParameters();

        for (java.lang.reflect.Parameter param : params) {
            if (param.getType() == JdaCommandContext.class) continue;

            Parameter annotation = param.getAnnotation(Parameter.class);
            String optName = resolveParamName(param, annotation).toLowerCase().replace(" ", "_");
            String optDesc = annotation != null ? annotation.description() : "No description provided.";
            boolean required = annotation == null || annotation.required();

            JdaArgumentParser<?> parser = registry.getParser(param.getType());
            OptionData opt = new OptionData(parser.optionType(), optName, optDesc, required);
            if (parser.supportsAutocomplete()) opt.setAutoComplete(true);
            options.add(opt);
        }

        return options;
    }

    private void applyPermission(SlashCommandData cmd) {
        Permission perm =
            commandClass.getAnnotation(Permission.class);
        if (perm != null) {
            cmd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(perm.value()));
        }
    }

    private CommandRoute route(Class<? extends JdaCommand> clazz, Method method) {
        boolean async = clazz.isAnnotationPresent(Async.class) || method.isAnnotationPresent(Async.class);
        return new CommandRoute(clazz, method, async);
    }

    private List<Method> getCommandMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(Command.class) && Modifier.isPublic(m.getModifiers()))
            .toList();
    }

    private List<Class<?>> getCommandNestedClasses(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredClasses())
            .filter(c -> c.isAnnotationPresent(Command.class)
                && Modifier.isStatic(c.getModifiers())
                && JdaCommand.class.isAssignableFrom(c))
            .toList();
    }

    private Method findExecuteMethod(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> m.getName().equals("execute") && Modifier.isPublic(m.getModifiers()))
            .findFirst()
            .orElse(null);
    }

    private String resolveParamName(java.lang.reflect.Parameter param, Parameter annotation) {
        if (annotation != null && !annotation.value().isBlank()) return annotation.value();
        String name = param.getName();
        if (name.startsWith("arg")) return param.getType().getSimpleName().toLowerCase();
        return name;
    }

    private String descOrDefault(String desc) {
        return desc.isBlank() ? "No description provided." : desc;
    }
}
