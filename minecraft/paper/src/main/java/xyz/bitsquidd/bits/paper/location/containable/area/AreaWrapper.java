/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.area;

public abstract class AreaWrapper {
    private final Area area;

    protected AreaWrapper(Area area) {
        this.area = area;
    }

    public Area area() {
        return area;
    }

}
