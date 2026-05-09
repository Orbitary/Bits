/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.util;

import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.audience.Audience;

import xyz.bitsquidd.bits.mc.command.annotation.Command;
import xyz.bitsquidd.bits.mc.sendable.text.Text;

/**
 * Utility class to encapsulate the command context during execution.
 * <p>
 * This class provides access to the underlying Brigadier context as well as the command sender
 * and the parsed arguments. It is passed to command methods annotated with
 * {@link Command}.
 * <p>
 * Example usage:
 * <pre>{@code
 * @Command(value = "teleport", description = "Teleports players")
 * public class TeleportCommand {
 *     @Command
 *     public void teleport(BitsCommandContext<?> ctx) {
 *         Player player = ctx.getSender();
 *         ctx.respond(Text.of("Teleporting..."));
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of the platform's original source object
 *
 * @since 0.0.10
 */
public abstract class BitsCommandContext<T> {
    protected final CommandContext<T> brigadierContext;
    protected final BitsCommandSourceContext<T> source;

    /**
     * @param brigadierContext the underlying Brigadier context
     *
     * @since 0.0.10
     */
    public BitsCommandContext(CommandContext<T> brigadierContext) {
        this.brigadierContext = brigadierContext;
        this.source = createSourceContext(brigadierContext);
    }

    /**
     * Creates a platform-specific source context from the given Brigadier context.
     *
     * @param brigadierContext the underlying Brigadier context
     *
     * @return the newly created source context
     *
     * @since 0.0.10
     */
    protected abstract BitsCommandSourceContext<T> createSourceContext(CommandContext<T> brigadierContext);


    /**
     * Returns the wrapper context for the command source.
     *
     * @return the source context
     *
     * @since 0.0.10
     */
    public BitsCommandSourceContext<T> getSource() {
        return source;
    }

    /**
     * Returns the underlying Brigadier command context.
     *
     * @return the Brigadier context
     *
     * @since 0.0.10
     */
    public CommandContext<T> getBrigadierContext() {
        return brigadierContext;
    }

    /**
     * Returns the command sender converted to an Adventure audience.
     *
     * @param <S> the type of the audience
     *
     * @return the command sender
     *
     * @since 0.0.10
     */
    public abstract <S extends Audience> S getSender();

    /**
     * Sends a message directly to the command sender.
     *
     * @param message the text message to send
     *
     * @since 0.0.10
     */
    public final void respond(Text message) {
        message.send(getSender());
    }

    /**
     * Retrieves the raw string argument at a specific index in the command input.
     *
     * @param index the 0-based index of the argument
     *
     * @return the String argument at the index, or an empty string if out of bounds
     *
     * @since 0.0.10
     */
    public String getValueAtIndex(int index) {
        try {
            String[] parts = getFullInput().split(" ");
            return parts[index];
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * Retrieves the last input segment of the command's full input.
     *
     * @return the final argument provided, or an empty string if no input
     *
     * @since 0.0.10
     */
    public String getLastInput() {
        String input = getFullInput();
        String[] parts = input.split(" ");
        if (parts.length == 0) return "";
        return parts[parts.length - 1];
    }

    /**
     * Returns the complete unparsed input string for the command.
     *
     * @return the full input string
     *
     * @since 0.0.10
     */
    public String getFullInput() {
        return brigadierContext.getInput();
    }

}