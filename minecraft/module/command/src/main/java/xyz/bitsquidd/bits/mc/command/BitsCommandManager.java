/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.BitsMinecraft;
import xyz.bitsquidd.bits.exception.BitsException;
import xyz.bitsquidd.bits.lifecycle.manager.BitsModule;
import xyz.bitsquidd.bits.mc.command.annotation.Command;
import xyz.bitsquidd.bits.mc.command.argument.BitsArgumentRegistry;
import xyz.bitsquidd.bits.mc.command.requirement.BitsRequirementRegistry;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;
import xyz.bitsquidd.bits.mc.permission.Permission;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;
import xyz.bitsquidd.bits.util.reflection.ScannerFlags;
import xyz.bitsquidd.bits.wrapper.collection.AddableSet;

import java.util.HashSet;
import java.util.Set;

// TODO:
//  - Allow for @Range annotations for Numbers.

/**
 * Manages the registration and lifecycle of all {@link BitsCommand}s.
 * <p>
 * Platform implementations extend this class to bind their specific platform contexts and argument
 * types to the Brigadier tree. This manager handles registering commands, resolving parameter parsers,
 * and maintaining the central command registry.
 * <p>
 * Example usage:
 * <pre>{@code
 * public class PlatformCommandManager extends BitsCommandManager<PlatformSource> {
 *     @Override
 *     protected void enableAllCommands() {
 *         // Register parsed commands to platform dispatcher...
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of the platform's original source object
 *
 * @since 0.0.10
 */
public abstract class BitsCommandManager<T> implements BitsModule {
    private static @Nullable BitsCommandManager<?> instance;

    protected final BitsArgumentRegistry<T> argumentRegistry;
    protected final BitsRequirementRegistry<T> requirementRegistry;
    protected final BrigadierTreeGenerator<T> brigadierTreeGenerator;

    private final Set<BitsCommand> registeredCommands = new HashSet<>();
    private final Permission commandBasePermission = initialiseBasePermission();

    protected BitsCommandManager() {
        if (instance != null) throw BitsException.INSTANCE_ALREADY_EXISTS(BitsCommandManager.class);
        BitsCommandManager.instance = this;

        this.argumentRegistry = initialiseArgumentRegistry();
        this.requirementRegistry = initialiseRequirementRegistry();
        this.brigadierTreeGenerator = new BrigadierTreeGenerator<>(this);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BitsCommandManager<?>> T get() {
        if (instance == null) throw BitsException.INSTANCE_NOT_FOUND(BitsCommandManager.class);
        return (T)instance;
    }

    /**
     * Registers the command listener and enables all commands.
     * <p>
     * Ensure this method is run on initialisation or plugin enable.
     *
     * @since 0.0.10
     */
    @Override
    public void startup() {
        enableAllCommands();
    }


    //region Registries

    /**
     * Initialises the argument registry responsible for parsing command inputs.
     *
     * @return the argument registry instance
     *
     * @since 0.0.10
     */
    protected abstract BitsArgumentRegistry<T> initialiseArgumentRegistry();

    /**
     * Returns the argument registry used by this manager.
     *
     * @return the argument registry
     *
     * @since 0.0.10
     */
    public final BitsArgumentRegistry<T> getArgumentRegistry() {
        return argumentRegistry;
    }

    /**
     * Initialises the requirement registry responsible for checking command permissions and state.
     *
     * @return the requirement registry instance
     *
     * @since 0.0.10
     */
    protected abstract BitsRequirementRegistry<T> initialiseRequirementRegistry();

    /**
     * Returns the requirement registry used by this manager.
     *
     * @return the requirement registry
     *
     * @since 0.0.10
     */
    public final BitsRequirementRegistry<T> getRequirementRegistry() {
        return requirementRegistry;
    }
    //endregion


    //region Context

    /**
     * Creates a new {@link BitsCommandContext} for the given {@link CommandContext}.
     * <p>
     * This can be overridden to provide custom context implementations (e.g., formatting command responses).
     *
     * @param brigadierContext the Brigadier context
     *
     * @return a new command context
     *
     * @since 0.0.10
     */
    public abstract BitsCommandContext<T> createContext(CommandContext<T> brigadierContext);

    /**
     * Creates a new platform-specific source context.
     *
     * @param sourceStack the platform source stack
     *
     * @return a new source context
     *
     * @since 0.0.10
     */
    public abstract BitsCommandSourceContext<T> createSourceContext(T sourceStack);
    //endregion


    //region Commands

    /**
     * Gets all currently registered commands.
     *
     * @return a set of all registered commands
     *
     * @since 0.0.10
     */
    public final Set<BitsCommand> getRegisteredCommands() {
        return registeredCommands;
    }

    /**
     * Registers a command to be enabled on startup.
     *
     * @param command the command to register
     *
     * @since 0.0.10
     */
    protected final void registerCommand(BitsCommand command) {
        registeredCommands.add(command);
    }
    //endregion


    /**
     * Provides all {@link BitsCommand}s to be registered on startup of the manager.
     *
     * @return a collection of all commands to be registered
     *
     * @since 0.0.10
     */
    protected AddableSet<BitsCommand> getAllCommands() {
        return AddableSet.of(ReflectionUtils.General.createAnnotatedClassesInDir("*", Command.class, BitsCommand.class, ScannerFlags.DEFAULT));
    }

    /**
     * Defines the base permission string for all commands.
     * <p>
     * For example, if returning the string {@code bits.command}, all commands will have
     * the permission base {@code bits.command.[command_name]}.
     *
     * @return the base permission string for all commands
     *
     * @since 0.0.10
     */
    protected Permission initialiseBasePermission() {
        return Permission.of("bits.command"); // The base prefix for all commands, can be overridden.
    }

    /**
     * Returns the base permission string for all commands.
     *
     * @return the base permission
     *
     * @since 0.0.10
     */
    public final Permission getCommandBasePermission() {
        return commandBasePermission;
    }


    /**
     * Main command executor handler. This must be implemented for commands to be executed.
     *
     * @param isAsync          whether the command is to be executed asynchronously
     * @param commandExecution the command execution runnable
     *
     * @since 0.0.10
     */
    protected final void executeCommand(boolean isAsync, Runnable commandExecution) {
        if (isAsync) {
            BitsMinecraft.get().runLaterAsync(commandExecution, 0);
        } else {
            BitsMinecraft.get().runLater(commandExecution, 0);
        }
    }


    /**
     * Creates a literal argument builder for the platform.
     *
     * @param name the literal name
     *
     * @return the literal argument builder
     *
     * @since 0.0.10
     */
    public LiteralArgumentBuilder<T> createLiteral(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a required argument builder for the platform.
     *
     * @param <W>  the argument type
     * @param name the argument name
     * @param type the argument type implementation
     *
     * @return the required argument builder
     *
     * @since 0.0.10
     */
    public <W> RequiredArgumentBuilder<T, W> createArgument(String name, ArgumentType<W> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }


    /**
     * Registers all generic {@link BitsCommand}s to the platform's dispatcher.
     *
     * @since 0.0.10
     */
    protected abstract void enableAllCommands();

}
