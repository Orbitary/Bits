/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.argument.BitsArgumentRegistry;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;


public class PaperBitsArgumentRegistry extends BitsArgumentRegistry<CommandSourceStack> {

    @Override
    protected @Nullable ArgumentType<?> toArgumentType(TypeSignature<?> inputType) {
        ArgumentType<?> superArgumentType = super.toArgumentType(inputType);
        if (superArgumentType != null) return superArgumentType;

        Class<?> clazz = inputType.toRawType();
        if (clazz == EntitySelector.class) {
            // Note net.minecraft.world.entity.EntitySelector and net.minecraft.commands.arguments.selector.EntitySelector are different things.
            // Our parsers expect a result in net.minecraft.commands.arguments.selector.EntitySelector.
            return EntityArgument.entities(); // TODO, in the future, we could consider refining this to be single/multiple/entity/player selectors. For now the parser should filter this.
        }

        return null;
    }

}
