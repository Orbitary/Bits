/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.audience.Audience;

import xyz.bitsquidd.bits.config.ClientBitsFabric;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandSourceContext;


public class FabricClientBitsCommandContext extends BitsCommandContext<FabricClientCommandSource> {
    public FabricClientBitsCommandContext(CommandContext<FabricClientCommandSource> brigadierContext) {
        super(brigadierContext);
    }

    @Override
    protected BitsCommandSourceContext<FabricClientCommandSource> createSourceContext(CommandContext<FabricClientCommandSource> brigadierContext) {
        return ClientBitsFabric.get().getCommandManager().createSourceContext(brigadierContext.getSource());
    }

    @Override
    public <S extends Audience> S getSender() {
        return getSource().getSender();
    }


}
