/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.velocitypowered.api.command.CommandSource;

import xyz.bitsquidd.bits.mc.command.requirement.BitsCommandRequirement;
import xyz.bitsquidd.bits.mc.command.requirement.BitsRequirementRegistry;

import java.util.Map;

public class VelocityBitsRequirementRegistry extends BitsRequirementRegistry<CommandSource> {

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
