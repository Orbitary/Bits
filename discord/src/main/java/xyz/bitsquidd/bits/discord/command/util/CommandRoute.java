package xyz.bitsquidd.bits.discord.command.util;

import org.jetbrains.annotations.Nullable;
import xyz.bitsquidd.bits.discord.command.JdaCommand;
import xyz.bitsquidd.bits.discord.command.JdaCommandGuard;

import java.lang.reflect.Method;

/**
 * Binds a resolved slash command route to the class and method to invoke.
 */
public record CommandRoute(
    Class<? extends JdaCommand> commandClass,
    Method method,
    boolean isAsync,
    @Nullable Class<? extends JdaCommandGuard> guardClass
) {}
