package xyz.bitsquidd.bits.discord.command.util;

/**
 * Composite key for routing slash command events to the correct method.
 */
public record RouteKey(String command, String group, String subcommand) {

    public static RouteKey of(String command, String group, String subcommand) {
        return new RouteKey(
            command,
            group != null ? group : "",
            subcommand != null ? subcommand : ""
        );
    }
}
