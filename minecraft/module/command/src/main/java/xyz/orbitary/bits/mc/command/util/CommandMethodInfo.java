/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command.util;

import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.mc.command.annotation.Async;
import xyz.orbitary.bits.mc.command.annotation.Command;
import xyz.orbitary.bits.mc.command.requirement.BitsCommandRequirement;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Stores reflection and metadata information about a specific command method.
 * <p>
 * This class parses method-level annotations and parameter types to help the command tree
 * generator understand how to invoke the method at runtime and what arguments it expects.
 * <p>
 * Example internal usage:
 * <pre>{@code
 * CommandMethodInfo<?> info = new CommandMethodInfo<>(method, classParams, corePermission);
 * if (info.isAsync()) {
 *     // Execute asynchronously
 * }
 * }</pre>
 *
 * @param <T> the type of the platform's original source object
 *
 * @since 0.0.10
 */
public class CommandMethodInfo<T> {
    private final Method method;

    private final boolean isAsync;
    private final boolean requiresContext;

    private final Command commandAnnotation;
    private final List<CommandParameterInfo> classParameters;
    private final List<CommandParameterInfo> methodParameters;
    private final List<CommandParameterInfo> allParameters;

    private final xyz.orbitary.bits.mc.permission.Permission corePermission;
    private @Nullable List<BitsCommandRequirement> requirements;

    public CommandMethodInfo(Method method, List<CommandParameterInfo> classParameters, xyz.orbitary.bits.mc.permission.Permission corePermission) {
        this.method = method;
        this.corePermission = corePermission;
        this.commandAnnotation = method.getAnnotation(Command.class);

        this.isAsync = method.isAnnotationPresent(Async.class);
        this.requiresContext = method.getParameterCount() > 0 && BitsCommandContext.class.isAssignableFrom(method.getParameters()[0].getType());

        // If the first parameter is a BitsCommandContext, we filter it, technically means we cant "parse" any BitsCommandContext args, this shouldn't be an issue...
        Parameter[] filteredParams = Arrays.stream(method.getParameters())
          .filter(param -> !BitsCommandContext.class.isAssignableFrom(param.getType()))
          .toArray(Parameter[]::new);
        List<CommandParameterInfo> methodParamInfos = new ArrayList<>();
        for (int i = 0; i < filteredParams.length; i++) {
            methodParamInfos.add(new CommandParameterInfo(filteredParams[i], i));
        }
        this.methodParameters = methodParamInfos;

        this.classParameters = new ArrayList<>(classParameters);

        this.allParameters = new ArrayList<>(this.classParameters);
        this.allParameters.addAll(this.methodParameters);
    }

    /**
     * Returns the underlying reflection method.
     *
     * @return the method
     *
     * @since 0.0.10
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Indicates whether the command should be executed asynchronously.
     *
     * @return true if marked with {@link Async}, false otherwise
     *
     * @since 0.0.10
     */
    public boolean isAsync() {
        return isAsync;
    }

    /**
     * Indicates whether the method's first parameter expects a context object.
     *
     * @return true if the first parameter is assignable from {@link BitsCommandContext}, false otherwise
     *
     * @since 0.0.10
     */
    public boolean requiresContext() {
        return requiresContext;
    }

    /**
     * Returns a list of parameters gathered from the enclosing command constructor.
     *
     * @return the class parameters
     *
     * @since 0.0.10
     */
    public List<CommandParameterInfo> getClassParameters() {
        return classParameters;
    }

    /**
     * Returns a list of parameters explicitly defined on the command method (excluding the context).
     *
     * @return the method-specific parameters
     *
     * @since 0.0.10
     */
    public List<CommandParameterInfo> getMethodParameters() {
        return methodParameters;
    }

    /**
     * Returns a combined list of all class and method parameters required for execution.
     *
     * @return all required parameters
     *
     * @since 0.0.10
     */
    public List<CommandParameterInfo> getAllParameters() {
        return allParameters;
    }

    /**
     * Returns the registered name mapping for the method from the {@link Command} annotation.
     *
     * @return the literal command string
     *
     * @since 0.0.10
     */
    public String literalName() {
        return commandAnnotation.value();
    }

    /**
     * Retrieves the requirements that must be met to invoke this method.
     * <p>
     * Resolved on first use, then cached.
     *
     * @return a list of parsed command requirements
     *
     * @since 0.0.10
     */
    public List<BitsCommandRequirement> getRequirements() {
        List<BitsCommandRequirement> resolved = requirements;
        if (resolved == null) {
            resolved = List.copyOf(RequirementCollector.collect(method, corePermission));
            requirements = resolved;
        }
        return resolved;
    }

}
