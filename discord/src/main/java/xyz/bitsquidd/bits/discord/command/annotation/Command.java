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
 * On a class: defines the root command. The class must extend {@link xyz.bitsquidd.bits.discord.command.JdaCommand}.
 * On a method: defines a subcommand. Nested static classes annotated {@code @Command} define subcommand groups.
 * <p>
 * A class with no {@code @Command}-annotated methods and no {@code @Command}-annotated nested classes
 * is a root command - the framework routes to its {@code execute()} method.
 * A class with annotated methods or nested classes is a group command.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String value();
    String description() default "";

    IntegrationType integrationType() default IntegrationType.GUILD_INSTALL;
    boolean nsfw() default false;
}
