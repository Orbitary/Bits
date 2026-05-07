/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import net.minecraft.commands.CommandSourceStack;

import xyz.bitsquidd.bits.mc.command.requirement.BitsCommandRequirement;
import xyz.bitsquidd.bits.mc.command.requirement.BitsRequirementRegistry;
import xyz.bitsquidd.bits.mc.command.requirement.ConsoleSenderRequirement;
import xyz.bitsquidd.bits.mc.command.requirement.PlayerSenderRequirement;

import java.util.Map;

public class PaperBitsRequirementRegistry extends BitsRequirementRegistry<CommandSourceStack> {

    @Override
    protected Map<Class<? extends BitsCommandRequirement>, BitsCommandRequirement> initialiseParsers() {
        Map<Class<? extends BitsCommandRequirement>, BitsCommandRequirement> parsers = super.initialiseParsers();

        parsers.putAll(Map.ofEntries(
          Map.entry(PlayerSenderRequirement.class, PlayerSenderRequirement.INSTANCE),
          Map.entry(ConsoleSenderRequirement.class, ConsoleSenderRequirement.INSTANCE)
        ));

        return parsers;
    }

}
