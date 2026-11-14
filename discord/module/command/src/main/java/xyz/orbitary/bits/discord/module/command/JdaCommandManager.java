/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import xyz.orbitary.bits.discord.module.command.argument.JdaArgumentParser;
import xyz.orbitary.bits.discord.module.command.argument.JdaArgumentRegistry;
import xyz.orbitary.bits.lifecycle.manager.BitsModule;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.module.command.CommandDefinition;
import xyz.orbitary.bits.module.command.CommandScanner;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;
import xyz.orbitary.bits.util.reflection.ScannerFlags;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Scans the classes annotated with {@link Command} and registers them as Discord slash commands.
 * Concrete {@link JdaArgumentParser} subclasses with a no-argument constructor are registered first, so commands can
 * take parameters of the parser's type.
 *
 * @since 0.1.6
 */
public final class JdaCommandManager implements BitsModule {

    private static final String BUILT_IN_PARSERS = JdaArgumentRegistry.class.getPackageName() + ".impl";

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
        registerScannedParsers();
        List<CommandDefinition> definitions = CommandScanner.scanAll(JdaCommandContext.class::equals);

        List<SlashCommandData> commands = new SlashCommandBuilder(arguments).build(definitions);
        Map<String, CommandDefinition> routes = definitions.stream()
          .collect(Collectors.toMap(command -> command.fullName(), command -> command));

        jda.get().updateCommands().addCommands(commands).queue(
          ok -> Logger.success("Registered " + commands.size() + " slash commands."),
          err -> Logger.exception("Failed to register slash commands", err)
        );
        jda.get().addEventListener(new SlashCommandDispatcher(routes, arguments));
    }

    @SuppressWarnings("rawtypes")
    private void registerScannedParsers() {
        for (Class<? extends JdaArgumentParser> type : ReflectionUtils.Scanner.tryGetClasses("*", JdaArgumentParser.class, ScannerFlags.DEFAULT)) {
            if (type.getPackageName().equals(BUILT_IN_PARSERS)) continue;
            ReflectionUtils.Instance.tryCreate(type).ifPresent(arguments::register);
        }
    }

}
