/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.util.UUID;

public final class ScannedArgument implements BrigadierArgument<Object> {
    protected ScannedArgument() {}

    @Override
    public Class<?> targetType() {
        return UUID.class;
    }

    @Override
    public ArgumentType<?> argumentType() {
        return StringArgumentType.word();
    }

    @Override
    public UUID read(CommandContext<Object> context, String name) {
        return UUID.fromString(StringArgumentType.getString(context, name));
    }
}
