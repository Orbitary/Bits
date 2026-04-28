/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.area.visualisation;

import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.PointVisualiser;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;


public final class Corner extends PointVisualiser {
    private Corner(BlockPos point) {
        super(point);
    }

    public static Corner of(BlockPos point) {
        return new Corner(point);
    }

    @Override
    protected int getColor() {
        return 0x0000ff;
    }

}
