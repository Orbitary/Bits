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

import xyz.bitsquidd.bits.mc.command.argument.InputTypeContainer;
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.exception.ExceptionBuilder;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class PlayerSingleArgumentParser extends AbstractArgumentParser<Player> {

    public PlayerSingleArgumentParser() {
        super(TypeSignature.of(Player.class), "Player");
    }

    @Override
    public Player parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        EntitySelector entitySelctor = singletonInputValidation(inputObjects, EntitySelector.class);

        return entitySelctor.findPlayers((CommandSourceStack)ctx.getBrigadierContext().getSource())
          .stream()
          .map(playerEntity -> playerEntity.getBukkitEntity().getPlayer())
          .map(Objects::requireNonNull)
          .findFirst()
          .orElseThrow(() -> ExceptionBuilder.createCommandException("Player not found: " + entitySelctor));
    }

    @Override
    public List<InputTypeContainer> getInputTypes() {
        return List.of(new InputTypeContainer(TypeSignature.of(EntitySelector.class), getArgumentName()));
    }

    @Override
    public Supplier<List<String>> getSuggestions() {
        return () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

}
