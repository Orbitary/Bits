/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.requirement;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;
import xyz.bitsquidd.bits.mc.sendable.text.Text;

public class PlayerSenderRequirement extends BitsCommandRequirement {
    public static final PlayerSenderRequirement INSTANCE = new PlayerSenderRequirement();

    protected PlayerSenderRequirement() {}

    @Override
    public boolean test(BitsCommandSourceContext<?> ctx) {
        return ctx.getSender() instanceof Player;
    }

    @Override
    public @Nullable Text getFailureMessage(BitsCommandSourceContext<?> ctx) {
        return Text.of(Component.text("This command can only be executed by a player."));
    }

}