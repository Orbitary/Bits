/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.discord.module.command.annotation;

import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.IntegrationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Discord-specific settings for the slash command defined by an annotated command class.
 *
 * @since 0.1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SlashSettings {

    /**
     * @return where the command can be installed
     *
     * @since 0.1.6
     */
    IntegrationType[] integrationTypes() default {IntegrationType.GUILD_INSTALL};

    /**
     * @return where the command can be used
     *
     * @since 0.1.6
     */
    InteractionContextType[] contexts() default {InteractionContextType.GUILD};

    /**
     * @return whether the command is marked as NSFW
     *
     * @since 0.1.6
     */
    boolean nsfw() default false;

}
