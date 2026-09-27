/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.command;

import xyz.orbitary.bits.discord.command.annotation.Command;

/**
 * Base class for all Discord slash commands.
 * <p>
 * Annotate the subclass with {@link Command} to register it.
 * <p>
 * Root command: annotate a method with {@code @Command("")} to handle the bare slash command.
 * Group command: annotate methods with {@code @Command("name")} for subcommands, or use annotated nested static classes for subcommand groups.
 */
public abstract class JdaCommand {}
