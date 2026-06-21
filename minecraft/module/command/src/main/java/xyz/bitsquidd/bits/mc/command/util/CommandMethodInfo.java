/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.util;

import xyz.bitsquidd.bits.mc.command.BitsCommandManager;
import xyz.bitsquidd.bits.mc.command.annotation.Async;
import xyz.bitsquidd.bits.mc.command.annotation.Command;
import xyz.bitsquidd.bits.mc.command.annotation.Permission;
import xyz.bitsquidd.bits.mc.command.annotation.Requirement;
import xyz.bitsquidd.bits.mc.command.requirement.BitsCommandRequirement;
import xyz.bitsquidd.bits.mc.command.requirement.impl.PermissionRequirement;

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
 * CommandMethodInfo<?> info = new CommandMethodInfo<>(method, classParams);
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

    public CommandMethodInfo(Method method, List<CommandParameterInfo> classParameters) {
        this.method = method;
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
        List<CommandParameterInfo> allParams = new ArrayList<>(classParameters);
        allParams.addAll(methodParameters);
        return allParams;
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

    //TODO: Merge with BitsCommandBuilder permission gathering?

    /**
     * Retrieves a compiled list of requirements that must be met to invoke this method.
     *
     * @param corePermission the base permission string for the command tree
     *
     * @return a list of parsed command requirements
     *
     * @since 0.0.10
     */
    public List<BitsCommandRequirement> getRequirements(xyz.bitsquidd.bits.mc.permission.Permission corePermission) {
        List<BitsCommandRequirement> requirements = new ArrayList<>();

        // Gather permission strings and convert them to requirements.
        Permission permissionAnnotation = method.getAnnotation(Permission.class);
        if (permissionAnnotation != null) {
            requirements.addAll(Arrays.stream(permissionAnnotation.value())
              .map(appended -> PermissionRequirement.of(xyz.bitsquidd.bits.mc.permission.Permission.of(corePermission + "." + appended)))
              .toList());
        }

        // Gather requirement instances
        Requirement requirementAnnotation = method.getAnnotation(Requirement.class);
        if (requirementAnnotation != null) {
            requirements.addAll(Arrays.stream(requirementAnnotation.value())
              .map(clazz -> BitsCommandManager.get().getRequirementRegistry().getRequirement(clazz))
              .toList());
        }

        return requirements;
    }

}
