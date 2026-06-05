/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.requirement;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;


public class PlayerSenderRequirement extends BitsCommandRequirement {
    public static final PlayerSenderRequirement INSTANCE = new PlayerSenderRequirement();

    protected PlayerSenderRequirement() {}

    @Override
    public boolean test(BitsCommandSourceContext<?> ctx) {
        return ctx.getSender() instanceof Player;
    }

    @Override
    public @Nullable Component getFailureMessage(BitsCommandSourceContext<?> ctx) {
        return Component.translatable("command.requirement.player_only.fail");
    }

}