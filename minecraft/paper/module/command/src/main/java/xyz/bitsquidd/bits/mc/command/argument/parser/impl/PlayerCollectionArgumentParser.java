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
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.mc.command.PaperBitsCommandContext;
import xyz.bitsquidd.bits.mc.command.argument.InputTypeContainer;
import xyz.bitsquidd.bits.mc.command.argument.parser.AbstractArgumentParser;
import xyz.bitsquidd.bits.mc.command.util.BitsCommandContext;
import xyz.bitsquidd.bits.wrapper.type.TypeSignature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PlayerCollectionArgumentParser extends AbstractArgumentParser<Collection<Player>> {

    // TODO: Just pull from vanilla EntitySelector. Also pull the completion.
    private enum SelectorType {
        ALL("@a", ctx -> new ArrayList<>(Bukkit.getOnlinePlayers())),
        SELF("@s", ctx -> List.of(((PaperBitsCommandContext)ctx).requirePlayer())),
        ;

        public final String selector;
        public final Function<BitsCommandContext<?>, Collection<Player>> playerFunction;

        SelectorType(String selector, Function<BitsCommandContext<?>, Collection<Player>> playerFunction) {
            this.selector = selector;
            this.playerFunction = playerFunction;
        }

        public static @Nullable SelectorType fromSelector(String selector) {
            for (SelectorType type : values()) {
                if (type.selector.equals(selector)) {
                    return type;
                }
            }
            return null;
        }

        public Collection<Player> get(BitsCommandContext<?> ctx) {
            return playerFunction.apply(ctx);
        }
    }

    public PlayerCollectionArgumentParser() {
        super(TypeSignature.of(Collection.class, Player.class), "Players");
    }

    @Override
    public Collection<Player> parse(List<Object> inputObjects, BitsCommandContext<?> ctx) throws CommandSyntaxException {
        EntitySelector entitySelctor = singletonInputValidation(inputObjects, EntitySelector.class);

        return entitySelctor.findPlayers((CommandSourceStack)ctx.getBrigadierContext().getSource())
          .stream()
          .map(playerEntity -> playerEntity.getBukkitEntity().getPlayer())
          .map(Objects::requireNonNull)
          .toList();
    }

    @Override
    public List<InputTypeContainer> getInputTypes() {
        return List.of(new InputTypeContainer(TypeSignature.of(EntitySelector.class), getArgumentName()));
    }

    @Override
    public Supplier<List<String>> getSuggestions() {
        List<String> suggestions = new ArrayList<>();
        suggestions.add(SelectorType.ALL.selector);
        suggestions.add(SelectorType.SELF.selector);

        suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        return () -> suggestions;
    }

}
