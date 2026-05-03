/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import xyz.bitsquidd.bits.mc.command.annotation.Command;
import xyz.bitsquidd.bits.mc.command.annotation.Requirement;
import xyz.bitsquidd.bits.mc.command.requirement.BitsCommandRequirement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Base class for all Bits commands.
 * <p>
 * Subclasses represent specific commands and can optionally group subcommands or provide dynamically
 * calculated requirements and permissions. Methods annotated with {@link Command}
 * inside subclasses are registered as executable endpoints for the command.
 * <p>
 * Example usage:
 * <pre>{@code
 * @Command(value = "teleport", aliases = {"tp"}, description = "Teleport players")
 * public final class TeleportCommand extends BitsCommand {
 *     @Command
 *     public void teleportCommand(BitsCommandContext<?> ctx) {
 *         ctx.respond(Text.of("Teleport command triggered!"), CommandReturnType.SUCCESS);
 *     }
 * }
 * }</pre>
 *
 * @since 0.0.10
 */
public abstract class BitsCommand {

    /**
     * Invoked when this command is successfully registered by the manager.
     * <p>
     * The default implementation does nothing. Subclasses can override this method to perform
     * initialisation logic, such as setting up dynamic permissions or states necessary for the command.
     *
     * @since 0.0.10
     */
    public void onRegister() {
        // Default implementation does nothing
        // Use this method to set up necessary states or permissions for roles dynamically.
    }

    /**
     * Provides additional, dynamically created requirements needed to execute this command.
     * <p>
     * Override this method to return condition-based requirements that cannot be defined cleanly
     * via the {@link Requirement} annotation.
     *
     * @return a collection of additional command requirements, never null
     *
     * @since 0.0.10
     */
    public Collection<BitsCommandRequirement> getAddedRequirements() {
        return new ArrayList<>();
    }

    /**
     * Provides alternative permission strings alongside the core permission automatically generated for this command.
     * <p>
     * Note: These permissions will not be prefixed with the core permission string of the command manager, acting
     * as complete overrides. Users possessing any of these permissions will be able to execute the command.
     *
     * @return a collection of alternate permission strings, never null
     *
     * @since 0.0.10
     */
    public Collection<String> getAlternatePermissionStrings() {
        return new ArrayList<>();
    }

}