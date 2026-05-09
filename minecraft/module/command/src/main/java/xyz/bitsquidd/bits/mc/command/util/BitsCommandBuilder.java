/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.util;


import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.BitsCommand;
import xyz.bitsquidd.bits.mc.command.BitsCommandManager;
import xyz.bitsquidd.bits.mc.command.annotation.Command;
import xyz.bitsquidd.bits.mc.command.annotation.Permission;
import xyz.bitsquidd.bits.mc.command.annotation.Requirement;
import xyz.bitsquidd.bits.mc.command.exception.CommandBuildException;
import xyz.bitsquidd.bits.mc.command.requirement.BitsCommandRequirement;
import xyz.bitsquidd.bits.mc.command.requirement.impl.PermissionRequirement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A builder utility that introspects {@link BitsCommand} classes via reflection.
 * <p>
 * This class extracts command methods, arguments, requirements, and permissions defined by annotations,
 * and helps the manager construct Brigadier nodes.
 * <p>
 * Example internal usage:
 * <pre>{@code
 * BitsCommandBuilder builder = new BitsCommandBuilder(TeleportCommand.class);
 * String name = builder.getCommandName();
 * List<Method> methods = builder.getCommandMethods();
 * }</pre>
 *
 * @since 0.0.10
 */
public final class BitsCommandBuilder {
    private @Nullable BitsCommand commandInstance;
    private final Class<? extends BitsCommand> commandClass;
    private final boolean isStaticClass;

    @SuppressWarnings("FieldCanBeLocal")
    private final Command commandAnnotation;

    private final String commandName;
    private final List<String> commandAliases;
    private final String commandDescription;

    private final xyz.bitsquidd.bits.mc.permission.Permission corePermission;
    private final List<xyz.bitsquidd.bits.mc.permission.Permission> permissions = new ArrayList<>();

    // Allow us to build from an instance or a class.
    // Instances are used only for gathering extra requirements.
    public BitsCommandBuilder(BitsCommand commandInstance) {
        this(Objects.requireNonNull(commandInstance).getClass());
        this.commandInstance = commandInstance;
        this.permissions.addAll(commandInstance.getAlternatePermissionStrings().stream().map(xyz.bitsquidd.bits.mc.permission.Permission::of).toList());
    }

    public BitsCommandBuilder(Class<? extends BitsCommand> commandClass) {
        this.commandClass = commandClass;
        this.isStaticClass = Modifier.isStatic(commandClass.getModifiers());

        commandAnnotation = commandClass.getAnnotation(Command.class);
        if (commandAnnotation == null) throw new CommandBuildException("Class " + commandClass + " must be annotated with @Command");
        commandName = commandAnnotation.value();
        commandAliases = List.of(commandAnnotation.aliases());
        commandDescription = commandAnnotation.description();

        this.corePermission = BitsCommandManager.get().getCommandBasePermission().append("." + commandName.replaceAll(" ", "_").toLowerCase());
        this.permissions.add(corePermission);
    }


    /**
     * Returns the command class being built.
     *
     * @return the command class
     *
     * @since 0.0.10
     */
    public Class<? extends BitsCommand> getCommandClass() {
        return commandClass;
    }

    /**
     * Returns the base name of the command.
     *
     * @return the command name
     *
     * @since 0.0.10
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Returns the aliases associated with the command.
     *
     * @return a list of aliases
     *
     * @since 0.0.10
     */
    public List<String> getCommandAliases() {
        return commandAliases;
    }

    /**
     * Returns the description of the command.
     *
     * @return the description
     *
     * @since 0.0.10
     */
    public String getCommandDescription() {
        return commandDescription;
    }


    /**
     * Returns a list of reflective parameters from the command's primary constructor.
     *
     * @return a list of parameters
     *
     * @since 0.0.10
     */
    public List<Parameter> getParameters() {
        Constructor<?>[] constructors = commandClass.getConstructors();
        if (constructors.length > 0) {
            Constructor<?> constructor = constructors[0];
            return Arrays.stream(constructor.getParameters())
              .filter(param -> !param.isSynthetic() && !BitsCommand.class.isAssignableFrom(param.getType()))
              .toList();
        }
        return Collections.emptyList();
    }

    /**
     * Extracts and returns all nested subcommand classes mapped within the command.
     *
     * @return a list of nested subcommand classes
     *
     * @since 0.0.10
     */
    @SuppressWarnings("unchecked")
    public List<Class<? extends BitsCommand>> getSubcommandClasses() {
        return Stream.of(commandClass.getDeclaredClasses())
          .filter(nestedClass -> BitsCommand.class.isAssignableFrom(nestedClass) && nestedClass.isAnnotationPresent(Command.class))
          .map(nestedClass -> (Class<? extends BitsCommand>)nestedClass)
          .collect(Collectors.toList());
    }

    /**
     * Extracts and returns all methods inside this class annotated as executable commands.
     *
     * @return a list of target methods
     *
     * @since 0.0.10
     */
    public List<Method> getCommandMethods() {
        return Arrays.stream(commandClass.getDeclaredMethods())
          .filter(method -> method.isAnnotationPresent(Command.class))
          .toList();
    }

    /**
     * Returns the primary constructor for the command class.
     *
     * @return the first declared constructor
     *
     * @since 0.0.10
     */
    public Constructor<?> toConstructor() {
        return commandClass.getDeclaredConstructors()[0];
    }


    /**
     * Compiles all requirements associated with the command from annotations or instances.
     *
     * @return a set of requirements ensuring safe execution
     *
     * @since 0.0.10
     */
    public Set<BitsCommandRequirement> getRequirements() {
        Set<BitsCommandRequirement> requirements = new HashSet<>();
        if (commandName.isEmpty()) requirements.add(PermissionRequirement.of(permissions));

        // Gather permission strings and convert them to requirements.
        Permission permissionAnnotation = commandClass.getAnnotation(Permission.class);
        if (permissionAnnotation != null) {
            requirements.addAll(Arrays.stream(permissionAnnotation.value())
              .map(appended -> PermissionRequirement.of(xyz.bitsquidd.bits.mc.permission.Permission.of(corePermission + "." + appended)))
              .toList());
        }

        // Gather requirement instances
        Requirement requirementAnnotation = commandClass.getAnnotation(Requirement.class);
        if (requirementAnnotation != null) {
            requirements.addAll(Arrays.stream(requirementAnnotation.value())
              .map(clazz -> BitsCommandManager.get().getRequirementRegistry().getRequirement(clazz))
              .toList());
        }

        if (commandInstance != null) requirements.addAll(commandInstance.getAddedRequirements());
        return requirements;
    }

    /**
     * Returns the base core permission configured for this command.
     *
     * @return the core permission
     *
     * @since 0.0.10
     */
    public xyz.bitsquidd.bits.mc.permission.Permission getCorePermission() {
        return corePermission;
    }

    /**
     * Returns an unmodifiable list of all permissions applying to this command.
     *
     * @return a list of permissions
     *
     * @since 0.0.10
     */
    public List<xyz.bitsquidd.bits.mc.permission.Permission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    /**
     * Checks if this command class requires an outer instance (i.e. is an inner non-static class).
     *
     * @return true if an outer instance is required
     *
     * @since 0.0.10
     */
    public boolean requiresOuterInstance() {
        return !isStaticClass && commandClass.isMemberClass();
    }

}
