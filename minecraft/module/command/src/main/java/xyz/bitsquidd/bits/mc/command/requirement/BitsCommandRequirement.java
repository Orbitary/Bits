/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.requirement;

import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.BitsCommand;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;
import xyz.bitsquidd.bits.mc.command.annotation.Requirement;
import xyz.bitsquidd.bits.mc.sendable.text.Text;

/**
 * A requirement that is checked before a command is executed, ensuring the sender has the specified permissions or state.
 * <p>
 * Implementations of this class can be applied to commands using the {@link Requirement}
 * annotation or dynamically via {@link BitsCommand#getAddedRequirements()}.
 * <p>
 * Example usage:
 * <pre>{@code
 * public class PlayerRequirement extends BitsCommandRequirement {
 *     @Override
 *     public boolean test(BitsCommandSourceContext<?> ctx) {
 *         // Custom check returning boolean
 *         return true;
 *     }
 * }
 * }</pre>
 *
 * @since 0.0.10
 */
public abstract class BitsCommandRequirement {
    /**
     * Tests whether the context meets this requirement.
     *
     * @param ctx the command source context representing the sender
     *
     * @return true if the requirement is met, false otherwise
     *
     * @since 0.0.10
     */
    public abstract boolean test(BitsCommandSourceContext<?> ctx);

    /**
     * Returns an optional failure message to display to the sender if this requirement is not met.
     *
     * @param ctx the command source context
     *
     * @return the failure message text, or null to suppress a message
     *
     * @since 0.0.10
     */
    public @Nullable Text getFailureMessage(BitsCommandSourceContext<?> ctx) {
        return null;
    }

}