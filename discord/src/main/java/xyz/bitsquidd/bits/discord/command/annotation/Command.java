/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.discord.command.annotation;

import net.dv8tion.jda.api.interactions.IntegrationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class or method as a Discord slash command.
 * <p>
 * On a class: defines the slash command name. The class must extend {@link xyz.bitsquidd.bits.discord.command.JdaCommand}.
 * On a method with a non-empty value: defines a subcommand.
 * On a method with {@code @Command("")}: marks the root handler for the command.
 * Nested static classes annotated {@code @Command} define subcommand groups.
 * <p>
 * If a {@code @Command("")} method and subcommands coexist, the root handler wins and subcommands are dropped with a warning.
 * If no {@code @Command("")} method exists and no subcommands are defined, the command is skipped with a warning.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String value();
    String description() default "";

    IntegrationType integrationType() default IntegrationType.GUILD_INSTALL;
    boolean nsfw() default false;
}
