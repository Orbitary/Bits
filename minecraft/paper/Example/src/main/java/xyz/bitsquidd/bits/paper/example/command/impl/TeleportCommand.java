package xyz.orbitary.bits.paper.example.command.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.orbitary.bits.command.BitsCommand;
import xyz.orbitary.bits.command.CommandReturnType;
import xyz.orbitary.bits.command.annotation.Command;
import xyz.orbitary.bits.command.annotation.Requirement;
import xyz.orbitary.bits.format.Formatter;
import xyz.orbitary.bits.sendable.text.Text;
import xyz.orbitary.bits.paper.example.command.CustomCommandContext;
import xyz.orbitary.bits.paper.lib.command.requirement.PlayerSenderRequirement;


/**
 * Example of a custom teleport command.
 * <ul>
 * <li>/teleport                                  - Sends confirmation that the teleport command is registered. </li>
 * <li>/teleport [player]                         - Teleports the sender to the target player. </li>
 * <li>/teleport [player] [entity]                - Teleports the target player to the target entity. </li>
 * <li>/teleport [player] [location]              - Teleports the target player to the target location. </li>
 * <li>/teleport [player] spawn                   - Teleports the target player to the world spawn location. </li>
 * <li>/teleport all [entity]                     - Teleports all players to the target entity. </li>
 * <li>/teleport all [location]                   - Teleports all players to the target location. </li>
 * <li>/teleport all spawn                        - Teleports all players to the world spawn location.</li>
 * </ul>
 */
@Command(value = "test-teleport", aliases = {"test-teleport-alias-1", "test-teleport-alias-2"}, description = "A test teleport command")
@Requirement(value = {PlayerSenderRequirement.class})
public final class TeleportCommand extends BitsCommand {

    @Command
    public void teleportTest(CustomCommandContext ctx) {
        ctx.respond(Text.of("This command works, well done!"), CommandReturnType.SUCCESS);
    }


    /**
     * Player-specific teleport commands.
     */
    @Command
    public final static class PlayerCommands extends BitsCommand {
        private final Player targetPlayer;

        public PlayerCommands(Player targetPlayer) {
            this.targetPlayer = targetPlayer;
        }

        @Requirement(PlayerSenderRequirement.class)
        @Command
        public void teleportToEntity(CustomCommandContext ctx) {
            Player senderPlayer = ctx.requirePlayer();

            if (targetPlayer.equals(senderPlayer)) {
                ctx.respond(Text.of("Cannot teleport " + targetPlayer.getName() + " to themselves!"), CommandReturnType.ERROR);
                return;
            }

            senderPlayer.teleport(targetPlayer.getLocation());
            ctx.respond(Text.of("Teleported " + senderPlayer.getName() + " to " + targetPlayer.getName()), CommandReturnType.SUCCESS);
        }

        @Command
        public void teleportToEntity(CustomCommandContext ctx, Player entity) {
            if (targetPlayer.equals(entity)) {
                ctx.respond(Text.of("Cannot teleport " + targetPlayer.getName() + " to themselves!"), CommandReturnType.ERROR);
                return;
            }

            targetPlayer.teleport(entity.getLocation());
            ctx.respond(Text.of("Teleported " + targetPlayer.getName() + " to " + entity.getName()), CommandReturnType.SUCCESS);
        }

        @Command
        public void teleportToLocation(CustomCommandContext ctx, Location location) {
            targetPlayer.teleport(location);
            ctx.respond(Text.of("Teleported " + targetPlayer.getName() + " to " + Formatter.format(location)), CommandReturnType.SUCCESS);
        }

        @Command("spawn")
        public void teleportToSpawn(CustomCommandContext ctx) {
            Location spawnLocation = targetPlayer.getWorld().getSpawnLocation();

            targetPlayer.teleport(spawnLocation);
            ctx.respond(Text.of("Teleported " + targetPlayer.getName() + " to spawn"), CommandReturnType.SUCCESS);
        }

    }


    /**
     * All-players teleport commands.
     */
    @Command("all")
    public final static class AllPlayersCommands extends BitsCommand {

        @Command
        public void teleportAllToEntity(CustomCommandContext ctx, Player entity) {
            Location targetLocation = entity.getLocation();

            int count = 0;
            for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (!player.equals(entity)) {
                    player.teleport(targetLocation);
                    count++;
                }
            }

            ctx.respond(Text.of("Teleported " + count + " players to " + entity.getName()), CommandReturnType.SUCCESS);
        }

        @Command
        public void teleportAllToLocation(CustomCommandContext ctx, Location location) {
            int count = 0;
            for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                player.teleport(location);
                count++;
            }

            ctx.respond(Text.of("Teleported " + count + " players to " + Formatter.format(location)), CommandReturnType.SUCCESS);
        }

        @Command("spawn")
        public void teleportAllToSpawn(CustomCommandContext ctx) {
            Location spawnLocation = ctx.requirePlayer().getWorld().getSpawnLocation();

            int count = 0;
            for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                player.teleport(spawnLocation);
                count++;
            }

            ctx.respond(Text.of("Teleported " + count + " players to spawn"), CommandReturnType.SUCCESS);
        }

    }

}