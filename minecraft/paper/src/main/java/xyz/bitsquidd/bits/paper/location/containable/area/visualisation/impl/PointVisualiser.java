/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl;

import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;

import java.util.Collections;
import java.util.Set;

public abstract class PointVisualiser extends RegionVisualiser {
    private final BlockPos point;

    protected PointVisualiser(BlockPos point) {
        this.point = point;
    }

    @Override
    protected Set<BlockPos> getPositions() {
        return Collections.singleton(point);
    }

}
