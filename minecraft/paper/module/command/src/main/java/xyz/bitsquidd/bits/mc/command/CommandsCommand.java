/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import xyz.bitsquidd.bits.mc.command.annotation.Command;
import xyz.bitsquidd.bits.mc.command.annotation.Requirement;
import xyz.bitsquidd.bits.mc.command.requirement.PlayerSenderRequirement;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


// TODO nice formatting on this!
@Command(
  value = "commands",
  description = "List commands available to you"
)
public class CommandsCommand extends BitsCommand {

    @Requirement(PlayerSenderRequirement.class)
    @Command
    public void execute(PaperBitsCommandContext ctx) {
        Player player = ctx.requirePlayer();

        List<BitsCommand> visible = BitsCommandManager.get().getRegisteredCommands().stream()
          .filter(cmd -> canView(player, cmd))
          .sorted(Comparator.comparing(CommandsCommand::commandName))
          .toList();

        Component message = Component.text("Commands", NamedTextColor.AQUA, TextDecoration.BOLD)
          .appendNewline();

        for (BitsCommand cmd : visible) {
            Command annotation = cmd.getClass().getAnnotation(Command.class);
            String desc = annotation != null ? annotation.description() : "";

            Component line = Component.text("/" + commandName(cmd), NamedTextColor.WHITE);
            if (!desc.isEmpty()) {
                line = line.append(Component.text(" - " + desc, NamedTextColor.GRAY));
            }
            message = message.append(line).appendNewline();
        }

        ctx.respond(message);
    }

    private static boolean canView(Player player, BitsCommand command) {
        Collection<String> perms = command.getAlternatePermissionStrings();
        return perms.isEmpty() || perms.stream().anyMatch(player::hasPermission);
    }

    private static String commandName(BitsCommand cmd) {
        Command annotation = cmd.getClass().getAnnotation(Command.class);
        return annotation != null ? annotation.value() : cmd.getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

}
