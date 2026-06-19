/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;


public final class BlockPosArgumentParser extends ArgumentParser<BlockPos, BlockPosArgumentParser.BlockPosData> {

    public record BlockPosData(
      double x,
      double y,
      double z
    ) {}

    public BlockPosArgumentParser() {
        super(TypeSignature.of(BlockPos.class), "BlockPos", BlockPosData.class);
    }


    @Override
    public BlockPos parse(BlockPosData data, BitsCommandContext<?> ctx) {
        return BlockPos.of(data.x(), data.y(), data.z());
    }

}
