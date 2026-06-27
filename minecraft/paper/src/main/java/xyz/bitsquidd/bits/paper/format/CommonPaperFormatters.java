/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.format;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.bitsquidd.bits.format.Formatter;


public final class CommonPaperFormatters {

    public static final Formatter.FormatterFunction<Player> PLAYER_NAME_FORMATTER = Player::getName;

    public static final Formatter.FormatterFunction<World> WORLD_NAME_FORMATTER = World::getName;

    public static final Formatter.FormatterFunction<Location> LOCATION_FORMATTER = loc -> String.format(
      "(%d, %d, %d) in %s",
      loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld() != null ? loc.getWorld().getName() : "null"
    );

    public static final Formatter.FormatterFunction<ItemStack> ITEMSTACK_FORMATTER =
      item -> String.format(
        "%s x%d",
        item.getType(),
        item.getAmount()
      );

    public static final Formatter.FormatterFunction<Material> MATERIAL_FORMATTER = Material::name;

    public static final Formatter.FormatterFunction<EntityType> ENTITYTYPE_FORMATTER = EntityType::name;

    public static final Formatter.FormatterFunction<Block> BLOCK_FORMATTER =
      block -> String.format(
        "%s at (%d, %d, %d)",
        block.getType(),
        block.getX(), block.getY(), block.getZ()
      );

    public static final Formatter.FormatterFunction<OfflinePlayer> OFFLINEPLAYER_FORMATTER =
      p -> String.format(
        "%s (%s) %s",
        p.getName(),
        p.getUniqueId(),
        p.isOnline() ? "[online]" : "[offline]"
      );

    public static void init() {
        Formatter.registerFormatter(Player.class, PLAYER_NAME_FORMATTER);
        Formatter.registerFormatter(World.class, WORLD_NAME_FORMATTER);
        Formatter.registerFormatter(Location.class, LOCATION_FORMATTER);
        Formatter.registerFormatter(ItemStack.class, ITEMSTACK_FORMATTER);
        Formatter.registerFormatter(Material.class, MATERIAL_FORMATTER);
        Formatter.registerFormatter(EntityType.class, ENTITYTYPE_FORMATTER);
        Formatter.registerFormatter(Block.class, BLOCK_FORMATTER);
        Formatter.registerFormatter(OfflinePlayer.class, OFFLINEPLAYER_FORMATTER);
    }

}
