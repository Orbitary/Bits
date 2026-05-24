package xyz.bitsquidd.bits.discord.command.util;

import xyz.bitsquidd.bits.discord.command.JdaCommand;

import java.lang.reflect.Method;

/**
 * Binds a resolved slash command route to the class and method to invoke.
 */
public record CommandRoute(
    Class<? extends JdaCommand> commandClass,
    Method method,
    boolean isAsync
) {}
