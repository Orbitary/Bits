/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import xyz.orbitary.bits.discord.module.command.argument.JdaArgumentRegistry;
import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.module.command.Command;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;
import xyz.orbitary.bits.util.reflection.ScannerFlags;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Scans the classes annotated with {@link Command} and registers them as Discord slash commands.
 *
 * @since 0.1.6
 */
public final class JdaCommandManager implements BitsModule {

    private final Supplier<JDA> jda;
    private final JdaArgumentRegistry arguments = JdaArgumentRegistry.withDefaults();

    /**
     * @param jda supplies the JDA instance to register commands with, once the platform has created it
     *
     * @since 0.1.6
     */
    public JdaCommandManager(Supplier<JDA> jda) {
        this.jda = jda;
    }

    @Override
    public void startup() {
        List<CommandDefinition> definitions = commandRoots().stream()
          .flatMap(root -> CommandScanner.scan(root, JdaCommandContext.class::equals).stream())
          .toList();

        List<SlashCommandData> commands = new SlashCommandBuilder(arguments).build(definitions);
        Map<String, CommandDefinition> routes = definitions.stream()
          .collect(Collectors.toMap(command -> command.fullName(), command -> command));

        jda.get().updateCommands().addCommands(commands).queue(
          ok -> Logger.success("Registered " + commands.size() + " slash commands."),
          err -> Logger.exception("Failed to register slash commands", err)
        );
        jda.get().addEventListener(new SlashCommandDispatcher(routes, arguments));
    }

    // Classes annotated with @Command or declaring a @Command method, skipping classes nested in another one
    // because the outer class's scan already covers them.
    private static List<Class<?>> commandRoots() {
        Set<Class<?>> candidates = new HashSet<>(ReflectionUtils.Scanner.tryGetAnnotatedClasses("*", Command.class, ScannerFlags.DEFAULT));
        for (Method method : ReflectionUtils.Scanner.tryGetAnnotatedMethods("*", Command.class, ScannerFlags.DEFAULT)) {
            candidates.add(method.getDeclaringClass());
        }
        return candidates.stream().filter(candidate -> !hasEnclosingCandidate(candidate, candidates)).toList();
    }

    private static boolean hasEnclosingCandidate(Class<?> type, Set<Class<?>> candidates) {
        for (Class<?> enclosing = type.getEnclosingClass(); enclosing != null; enclosing = enclosing.getEnclosingClass()) {
            if (candidates.contains(enclosing)) return true;
        }
        return false;
    }

}
