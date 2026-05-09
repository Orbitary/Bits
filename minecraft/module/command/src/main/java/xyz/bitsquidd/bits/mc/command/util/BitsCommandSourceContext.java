/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.util;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.ApiStatus;

import xyz.bitsquidd.bits.mc.sendable.text.Text;

/**
 * Utility class to encapsulate the underlying platform's command source stack.
 * <p>
 * This provides a unified way to interact with the entity that executed the command,
 * allowing messages to be sent regardless of the underlying platform abstraction.
 * <p>
 * Example usage:
 * <pre>{@code
 * BitsCommandSourceContext<?> sourceCtx = ctx.getSource();
 * sourceCtx.respond(Text.of("Command received!"));
 * }</pre>
 *
 * @param <T> the type of the platform's original source object
 *
 * @since 0.0.10
 */
public abstract class BitsCommandSourceContext<T> {
    protected final T source;

    /**
     * @param source the platform's command source object
     *
     * @since 0.0.10
     */
    public BitsCommandSourceContext(T source) {
        this.source = source;
    }

    /**
     * Returns the original platform source object.
     *
     * @return the platform source
     *
     * @since 0.0.10
     */
    public T getSource() {
        return source;
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
     * Sends a message to the command sender.
     * <p>
     * Experimental: this may be expanded in the future to handle command errors and success.
     *
     * @param message the message to send
     *
     * @since 0.0.10
     */
    @ApiStatus.Experimental
    public void respond(Text message) {
        message.send(getSender());
    }

}