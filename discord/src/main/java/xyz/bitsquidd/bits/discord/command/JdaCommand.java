/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command;

/**
 * Base class for all Discord slash commands.
 * <p>
 * Annotate the subclass with {@link xyz.bitsquidd.bits.discord.command.annotation.Command} to register it.
 * <p>
 * Root command: no {@code @Command}-annotated methods: implement {@code execute(JdaCommandContext, ...)}.
 * Group command: annotate methods with {@code  @Command} for subcommands, or use annotated nested static classes for subcommand groups.
 */
public abstract class JdaCommand {}
