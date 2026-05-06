/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;

import java.util.HashSet;
import java.util.Set;

public abstract class RegionVisualiser {
    private final Set<ItemDisplay> displays = new HashSet<>();


    protected abstract Set<BlockPos> getPositions();

    protected abstract int getColor();

    public void show(World world) {
        clear();
        getPositions().forEach(position -> {
            ItemDisplay display = world.spawn(position.asLocation(world), ItemDisplay.class);
            display.setItemStack(new ItemStack(Material.DIAMOND));
            displays.add(display);
        });
    }

    public void clear() {
        displays.forEach(ItemDisplay::remove);
        displays.clear();
    }

}
