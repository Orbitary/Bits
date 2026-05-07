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
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.BlockPosArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.LocationArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.PlayerCollectionArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.PlayerSingleArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.WorldArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive.EntitySelectorArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.impl.primitive.PrimitiveArgumentParser;
import xyz.bitsquidd.bits.wrapper.collection.AddableSet;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.ArrayList;
import java.util.List;


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

    @Override
    protected List<PrimitiveArgumentParser<?>> initialisePrimitiveParsers() {
        List<PrimitiveArgumentParser<?>> parsers = new ArrayList<>(super.initialisePrimitiveParsers());
        parsers.add(new EntitySelectorArgumentParser());
        return parsers;
    }

    @Override
    protected AddableSet<AbstractArgumentParser<?>> initialiseParsers() {
        return super.initialiseParsers().addAll(List.of(
          new WorldArgumentParser(),
          new LocationArgumentParser(),
          new BlockPosArgumentParser(),
          new PlayerCollectionArgumentParser(),
          new PlayerSingleArgumentParser()
        ));
    }

}
