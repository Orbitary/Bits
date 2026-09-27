/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;

import xyz.orbitary.bits.mc.command.util.BitsCommandSourceContext;

public class FabricClientBitsCommandSourceContext extends BitsCommandSourceContext<FabricClientCommandSource> {
    public FabricClientBitsCommandSourceContext(FabricClientCommandSource source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Audience getSender() {
        // Fabric's sender will always be the client.
        return MinecraftClientAudiences.of().audience();
    }

}
