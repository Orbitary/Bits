/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.argument.parser.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.bitsquidd.bits.mc.command.argument.parser.ArgumentParser;
import xyz.bitsquidd.bits.mc.command.argument.parser.SuggestionSupplier;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.Objects;


public final class PlayerSingleArgumentParser extends ArgumentParser<Player, EntitySelector> {

    public PlayerSingleArgumentParser() {
        super(TypeSignature.of(Player.class), "Player", EntitySelector.class);
    }

    @Override
    public Player parse(EntitySelector data, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        return data.findPlayers((CommandSourceStack)ctx.getBrigadierContext().getSource())
          .stream()
          .map(playerEntity -> playerEntity.getBukkitEntity().getPlayer())
          .map(Objects::requireNonNull)
          .findFirst()
          .orElseThrow(() -> ExceptionBuilder.createCommandException("Player not found: " + data));
    }

    @Override
    public <T> SuggestionSupplier<T> getSuggestions() {
        return _ -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

}
