/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command.util;

import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.mc.command.exception.CommandBuildException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Instantiates a command class and invokes one of its command methods with already parsed arguments.
 * <p>
 * Reflection targets are resolved once when the command tree is built, not on every execution.
 *
 * @since 0.1.0
 */
public final class CommandInvoker {
    private final Constructor<?> constructor;
    private final int constructorParamCount;
    private final @Nullable Class<?> outerClass; // Non-null only for inner (non-static) command classes.
    private final Method method;
    private final boolean requiresContext;

    public CommandInvoker(BitsCommandBuilder commandBuilder, CommandMethodInfo<?> methodInfo) {
        this.constructor = commandBuilder.toConstructor();
        this.constructorParamCount = commandBuilder.getParameters().size();
        this.outerClass = commandBuilder.requiresOuterInstance() ? commandBuilder.getCommandClass().getDeclaringClass() : null;
        this.method = methodInfo.getMethod();
        this.requiresContext = methodInfo.requiresContext();
    }

    /**
     * Invokes the command method.
     *
     * @param parsedArguments the parsed constructor arguments followed by the parsed method arguments
     * @param ctx             the command context, passed as the first method argument if the method requires it
     *
     * @since 0.1.0
     */
    public void invoke(List<@Nullable Object> parsedArguments, BitsCommandContext<?> ctx) {
        try {
            Object[] constructorArgs = parsedArguments.subList(0, constructorParamCount).toArray();

            if (outerClass != null) {
                Object outerInstance = outerClass.getDeclaredConstructor().newInstance();

                Object[] argsWithOuter = new Object[constructorArgs.length + 1];
                argsWithOuter[0] = outerInstance;
                System.arraycopy(constructorArgs, 0, argsWithOuter, 1, constructorArgs.length);
                constructorArgs = argsWithOuter;
            }

            Object instance = constructor.newInstance(constructorArgs);

            List<@Nullable Object> methodArguments = constructorParamCount < parsedArguments.size()
                                                     ? new ArrayList<>(parsedArguments.subList(constructorParamCount, parsedArguments.size()))
                                                     : new ArrayList<>();

            if (requiresContext) methodArguments.addFirst(ctx);

            method.invoke(instance, methodArguments.toArray());
        } catch (Exception e) {
            throw new CommandBuildException("Failed to execute command method: " + method.getName() + " ", e);
        }
    }

}
